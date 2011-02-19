/**
 * 
 */
package com.sipcm.sip.servlet;

import gov.nist.javax.sip.message.SIPRequest;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.util.DosProtector;
import com.sipcm.sip.util.ServerAuthenticationHelper;
import com.sipcm.sip.vendor.VoipVendorManager;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "CallCenterServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class CallCenterServlet extends AbstractSipServlet {
	private static final long serialVersionUID = -4659953196279153841L;

	@Autowired
	@Qualifier("serverAuthenticationHelper")
	private ServerAuthenticationHelper authenticationHelper;

	@Autowired
	@Qualifier("userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Autowired
	@Qualifier("sip.DosProtector")
	private DosProtector dosProtector;

	@Autowired
	@Qualifier("voipVendorManager")
	private VoipVendorManager vendorManager;

	@Override
	protected void doResponse(javax.servlet.sip.SipServletResponse resp)
			throws javax.servlet.ServletException, java.io.IOException {
		SipServletRequest req = resp.getRequest();
		if (req != null
				&& SIPRequest.REGISTER.equalsIgnoreCase(req.getMethod())) {
			vendorManager.handleRegisterResponse(resp);
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("Cannot find client register servlet, just drop it.");
			}
			return;
		}
		super.doResponse(resp);
	}

	/**
	 * DOS protector check.
	 */
	@Override
	protected void doRequest(javax.servlet.sip.SipServletRequest req)
			throws javax.servlet.ServletException, java.io.IOException {
		if (req.isInitial() && !specialHandleRequest(req)
				&& dosProtector.isDosAttach(req)) {
			return;
		}
		super.doRequest(req);
	}

	/**
	 * This will just forward request to RegistrarServlet
	 */
	@Override
	protected void doRegister(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get register request: {}", req);
		}
		UserSipProfile userSipProfile = checkAuthentication(req);
		if (userSipProfile == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Authentication failed, response should sent");
			}
			return;
		}
		req.setAttribute(USER_ATTRIBUTE, userSipProfile);
		RequestDispatcher dispatcher = req
				.getRequestDispatcher("RegistrarServlet");
		if (dispatcher != null) {
			dispatcher.forward(req, null);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Error! Cannot found registrar servlet.");
			}
			response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR,
					"Cannot found registrar servlet.");
		}
	}

	/**
	 * Main logic to dispatch to different servlet.
	 */
	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get invite request: {}", req);
		}
		if (req.isInitial()) {
			if (logger.isTraceEnabled()) {
				logger.trace("This is initial request.");
			}
			URI toURI = req.getTo().getURI();
			URI fromURI = req.getFrom().getURI();
			if (!toURI.isSipURI() || !fromURI.isSipURI()) {
				if (logger.isDebugEnabled()) {
					logger.debug("We only recognize SIP request. Will response unsupported uri scheme.");
				}
				response(req, SipServletResponse.SC_UNSUPPORTED_URI_SCHEME);
				return;
			}
			final SipURI toSipUri = (SipURI) toURI;
			final SipURI fromSipUri = (SipURI) fromURI;
			String toHost = toSipUri.getHost();
			String fromHost = fromSipUri.getHost();
			String toUser = toSipUri.getUser();

			if (fromHost.toUpperCase().endsWith(getDomain().toUpperCase())) {
				if (logger.isTraceEnabled()) {
					logger.trace("From host is the domain we served, check authentication.");
				}
				UserSipProfile userSipProfile = checkAuthentication(req);
				if (userSipProfile == null) {
					if (logger.isTraceEnabled()) {
						logger.trace("Not passing authentication, response should already send, just return.");
					}
					return;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Authentication pass, set user to request.");
				}
				req.setAttribute(USER_ATTRIBUTE, userSipProfile);
			}
			Collection<AddressBinding> bindings = null;
			if ((bindings = vendorManager.isLocalUsr(toHost, toUser)) != null) {
				req.setAttribute(TARGET_USERSIPBINDING, bindings);
				if (logger.isTraceEnabled()) {
					logger.trace("This is a incoming invite to local user.");
				}
				RequestDispatcher dispatcher = req
						.getRequestDispatcher("IncomingInviteServlet");
				if (dispatcher != null) {
					dispatcher.forward(req, null);
					return;
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Cannot find incoming invite servlet, response server internal error.");
					}
					response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
					return;
				}
			} else if (phoneNumberUtil.isValidPhoneNumber(toUser)) {
				UserSipProfile user = (UserSipProfile) req
						.getAttribute(USER_ATTRIBUTE);
				if (user != null) {
					req.setAttribute(CALLING_PHONE_NUMBER, phoneNumberUtil
							.getCanonicalizedPhoneNumber(toUser,
									user.getDefaultAreaCode()));
					RequestDispatcher dispatcher = req
							.getRequestDispatcher("OutgoingPhoneInviteServlet");
					if (dispatcher != null) {
						dispatcher.forward(req, null);
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("Cannot find outgoing phone invite servlet, response server internal error.");
						}
						response(req,
								SipServletResponse.SC_SERVER_INTERNAL_ERROR);
					}
					return;
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Only authenticated user can call phone number.");
					}
					response(req, SipServletResponse.SC_BAD_REQUEST);
					return;
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Cannot accept this INVITE");
				}
				response(req, SipServletResponse.SC_NOT_FOUND);
				return;
			}
		} else {
			super.doInvite(req);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doOptions(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doOptions(SipServletRequest req) throws ServletException,
			IOException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Got options request: {}", req);
			}
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_OK);
			if (logger.isTraceEnabled()) {
				logger.trace("Sending response back: {}", response);
			}
			response.send();
		} finally {
			SipSession session = req.getSession(false);
			if (session != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Invalidate session: {}", session.getId());
				}
				session.invalidate();
			}
		}
	}

	private UserSipProfile checkAuthentication(SipServletRequest req)
			throws IOException {
		UserSipProfile userSipProfile = null;
		if (authenticationHelper.authenticate(req)) {
			Principal principal = req.getUserPrincipal();
			String username = principal.getName();
			userSipProfile = userSipProfileService
					.getUserSipProfileByUsername(username);
			if (userSipProfile == null) {
				response(req, SipServletResponse.SC_NOT_FOUND);
			}
			dosProtector.resetCounter(req);
		} else {
			dosProtector.countAuthFailure(req);
		}
		return userSipProfile;
	}
}
