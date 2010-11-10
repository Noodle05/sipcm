/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.security.Principal;

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
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.util.ServerAuthenticationHelper;

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
	 * Main logic on dispatch to different servlet.
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
			if (!getDomain().equalsIgnoreCase(toHost)
					&& !getDomain().equalsIgnoreCase(fromHost)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Not from host nor to host is domain we served, response as forbidden.");
				}
				response(req, SipServletResponse.SC_FORBIDDEN,
						"Do not serve your domain.");
				return;
			}

			if (getDomain().equalsIgnoreCase(fromHost)) {
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
			String toUser = toSipUri.getUser();
			if (getDomain().equalsIgnoreCase(toHost)) {
				if (phoneNumberUtil.isValidPhoneNumber(toUser)) {
					UserSipProfile user = (UserSipProfile) req
							.getAttribute(USER_ATTRIBUTE);
					req.setAttribute(CALLING_PHONE_NUMBER, phoneNumberUtil
							.getCanonicalizedPhoneNumber(
									toUser,
									user == null ? null : user
											.getDefaultAreaCode()));
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
						response(req,
								SipServletResponse.SC_SERVER_INTERNAL_ERROR);
						return;
					}
				}
			}

			if (logger.isTraceEnabled()) {
				logger.trace("Calling other voip user, forward to proxy servlet.");
			}

			RequestDispatcher dispatcher = req
					.getRequestDispatcher("ProxyServlet");
			if (dispatcher != null) {
				dispatcher.forward(req, null);
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Cannot find forward servlet, response server internal error.");
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
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
		}
		return userSipProfile;
	}
}
