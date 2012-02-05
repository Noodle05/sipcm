/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.message.Request;

import com.mycallstation.dataaccess.business.RoleService;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.sip.events.CallEndEvent;
import com.mycallstation.sip.events.CallStartEvent;
import com.mycallstation.sip.keepalive.PhoneNumberKeepAlive;
import com.mycallstation.sip.locationservice.UserBindingInfo;
import com.mycallstation.sip.util.DosProtector;
import com.mycallstation.sip.util.ServerAuthenticationHelper;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * @author Wei Gao
 * 
 */
@SipServlet(name = "CallCenterServlet", applicationName = "com.mycallstation.CallCenter", loadOnStartup = 1)
public class CallCenterServlet extends AbstractSipServlet {
	private static final long serialVersionUID = -4659953196279153841L;

	@Resource(name = "serverAuthenticationHelper")
	private ServerAuthenticationHelper authenticationHelper;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "sipDosProtector")
	private DosProtector dosProtector;

	@Resource(name = "phoneNumberKeepAlive")
	private PhoneNumberKeepAlive keepAlive;

	@Override
	protected void doResponse(javax.servlet.sip.SipServletResponse resp)
			throws javax.servlet.ServletException, java.io.IOException {
		SipServletRequest req = resp.getRequest();
		if (req != null && Request.REGISTER.equalsIgnoreCase(req.getMethod())) {
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

			if (fromHost.toUpperCase().endsWith(
					appConfig.getDomain().toUpperCase())) {
				if (logger.isTraceEnabled()) {
					logger.trace("From host is the domain we served, check authentication.");
				}
				UserSipProfile userSipProfile = checkAuthentication(req);
				if (userSipProfile == null) {
					if (logger.isTraceEnabled()) {
						logger.trace("Authentication failed, response should already send, just return.");
					}
					return;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Authentication success, set user to request.");
				}
				req.setAttribute(USER_ATTRIBUTE, userSipProfile);
			}
			if (vendorManager.handleInvite(req, toHost, toUser)) {
				if (logger.isTraceEnabled()) {
					logger.trace("This is a incoming invite to local user.");
				}
				UserSipProfile userSipProfile = null;
				if (req.getAttribute(TARGET_USERSIPBINDING) != null) {
					UserBindingInfo ubi = (UserBindingInfo) req
							.getAttribute(TARGET_USERSIPBINDING);
					if (ubi.getAccount() != null) {
						userSipProfile = ubi.getAccount().getOwner();
					} else {
						userSipProfile = ubi.getBindings().iterator().next()
								.getUserSipProfile();
					}
				} else if (req.getAttribute(USER_VOIP_ACCOUNT) != null) {
					userSipProfile = ((UserVoipAccount) req
							.getAttribute(USER_VOIP_ACCOUNT)).getOwner();
				} else if (req.getAttribute(USER_ATTRIBUTE) != null) {
					userSipProfile = (UserSipProfile) req
							.getAttribute(USER_ATTRIBUTE);
				}
				if (userSipProfile != null
						&& keepAlive.receiveCall(userSipProfile,
								fromSipUri.getUser())) {
					// This incoming invite is the response of keep alive
					// request, forward to keep alive servlet.
					RequestDispatcher dispatcher = req
							.getRequestDispatcher("KeepAliveServlet");
					if (dispatcher != null) {
						req.setAttribute(USER_ATTRIBUTE, userSipProfile);
						dispatcher.forward(req, null);
					} else {
						if (logger.isErrorEnabled()) {
							logger.error("Cannot find keep alive servlet, response server internal error.");
						}
						response(req,
								SipServletResponse.SC_SERVER_INTERNAL_ERROR);
					}
					return;
				}

				if (req.getAttribute(TARGET_USERSIPBINDING) != null) {
					RequestDispatcher dispatcher = req
							.getRequestDispatcher("IncomingInviteServlet");
					if (dispatcher != null) {
						dispatcher.forward(req, null);
					} else {
						if (logger.isErrorEnabled()) {
							logger.error("Cannot find incoming invite servlet, response server internal error.");
						}
						response(req,
								SipServletResponse.SC_SERVER_INTERNAL_ERROR);
					}
					return;
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("However user is not registered yet, response temporarly unavaliable.");
					}
					response(req, SipServletResponse.SC_TEMPORARLY_UNAVAILABLE);
					String fromUser = fromSipUri.getUser();
					if (PhoneNumberUtil.isValidPhoneNumber(fromUser)) {
						callFailed(
								((UserSipProfile) req
										.getAttribute(USER_ATTRIBUTE)),
								((UserVoipAccount) req
										.getAttribute(USER_VOIP_ACCOUNT)),
								PhoneNumberUtil
										.getCanonicalizedPhoneNumber(fromUser),
								SipServletResponse.SC_TEMPORARLY_UNAVAILABLE,
								"User not logged in");
					}
					return;
				}
			} else if (PhoneNumberUtil.isValidPhoneNumber(toUser)) {
				UserSipProfile user = (UserSipProfile) req
						.getAttribute(USER_ATTRIBUTE);
				if (user != null) {
					req.setAttribute(CALLING_PHONE_NUMBER, PhoneNumberUtil
							.getCanonicalizedPhoneNumber(toUser,
									user.getDefaultAreaCode()));
					RequestDispatcher dispatcher = req
							.getRequestDispatcher("OutgoingPhoneInviteServlet");
					if (dispatcher != null) {
						dispatcher.forward(req, null);
					} else {
						if (logger.isErrorEnabled()) {
							logger.error("Cannot find outgoing phone invite servlet, response server internal error.");
						}
						response(req,
								SipServletResponse.SC_SERVER_INTERNAL_ERROR);
					}
					return;
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn(
								"Only authenticated user can call phone number, from URI: \"{}\"",
								fromURI);
					}
					response(req, SipServletResponse.SC_BAD_REQUEST);
					dosProtector.countAttack(req);
					return;
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Cannot accept this INVITE from ip: \"{}\". Request: \"{}\"",
							req.getInitialRemoteAddr(), req);
				}
				response(req, SipServletResponse.SC_FORBIDDEN);
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
			if (req.isUserInRole(RoleService.CALLER_ROLE)) {
				Principal principal = req.getUserPrincipal();
				String username = principal.getName();
				userSipProfile = userSipProfileService
						.getUserSipProfileByUsername(username);
				if (userSipProfile == null) {
					response(req, SipServletResponse.SC_NOT_FOUND);
				}
				dosProtector.resetCounter(req);
			} else {
				response(req, SipServletResponse.SC_FORBIDDEN);
			}
		} else {
			dosProtector.countAttack(req);
		}
		return userSipProfile;
	}

	protected void callFailed(UserSipProfile usp, UserVoipAccount account,
			String partner, int status, String reason) {
		CallStartEvent se = null;
		if (usp != null) {
			se = new CallStartEvent(usp, partner);
		} else if (account != null) {
			se = new CallStartEvent(account, partner);
		}
		if (se != null) {
			CallEndEvent ee = new CallEndEvent(se, status, reason);
			callEventListener.incomingCallFailed(ee);
		}
	}
}
