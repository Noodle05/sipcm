/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;

import org.mobicents.servlet.sip.core.session.MobicentsSipSession;
import org.mobicents.servlet.sip.message.B2buaHelperImpl;
import org.mobicents.servlet.sip.message.SipServletRequestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.common.model.User;
import com.sipcm.googlevoice.GoogleVoiceManager;
import com.sipcm.googlevoice.GoogleVoiceSession;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.util.GvB2buaHelperImpl;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "GoogleVoiceServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class GoogleVoiceServlet extends B2bServlet {
	private static final long serialVersionUID = 1812855574907498697L;

	public static final String ORIGINAL_SESSION = "com.sipcm.original.session";
	public static final String GV_SESSION = "com.sipcm.googlevoice.session";
	public static final String ORIGINAL_REQUEST = "com.sipcm.originalRequest";

	@Autowired
	@Qualifier("googleVoiceManager")
	private GoogleVoiceManager googleVoiceManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.servlet.B2bServlet#doInvite(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get invite request: {}", req);
		}
		SipURI toUri = (SipURI) req.getTo().getURI();
		String toUser = toUri.getUser();
		if (PHONE_NUMBER.matcher(toUser).matches()) {
			// This is initial call
			Principal p = req.getUserPrincipal();
			if (p == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot found login prinipal for outgoing call? this should never happen.");
				}
				responseError(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			// String username = p.getName();
			SipApplicationSession appSession = req.getApplicationSession();
			getServletContext().setAttribute(generateAppSessionKey(req, true),
					appSession.getId());
			User user = (User) req.getAttribute(USER_ATTRIBUTE);
			UserVoipAccount account = (UserVoipAccount) req
					.getAttribute(USER_VOIP_ACCOUNT);
			if (user == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot found user from request? This should never happen.");
				}
				responseError(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			if (account == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot found voip account for {}? This should never happen");
				}
				responseError(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			processGoogleVoiceCall(req, appSession, user, account, toUser);
		} else {
			// This is call back.
			String appSessionIdKey = generateAppSessionKey(req, false);
			String appSessionId = (String) getServletContext().getAttribute(
					appSessionIdKey);
			if (appSessionId == null) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Cannot found appSessionId for {}? This should never happen",
							appSessionIdKey);
				}
				responseError(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			SipApplicationSession appSession = sipSessionsUtil
					.getApplicationSessionById(appSessionId);
			if (appSession == null) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Cannot found application session for {}? This should never happen",
							toUser);
				}
				responseError(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			getServletContext().removeAttribute(appSessionIdKey);
			appSession.removeAttribute(GV_WAITING_FOR_CALLBACK);
			appSession.removeAttribute(GV_SESSION);
			SipSession session = req.getSession();
			SipSession origSession = (SipSession) appSession
					.getAttribute(ORIGINAL_SESSION);
			B2buaHelper helper = getB2buaHelper(req);
			helper.linkSipSessions(session, origSession);
			SipServletRequest origReq = (SipServletRequest) origSession
					.getAttribute(ORIGINAL_REQUEST);
			// Response OK to original request first.
			SipServletResponse origResponse = origReq
					.createResponse(SipServletResponse.SC_OK);
			copyContent(req, origResponse);
			origResponse.send();
			// Response OK to this request.
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_OK);
			copyContent(origReq, response);
			response.send();
		}
	}

	private void processGoogleVoiceCall(SipServletRequest req,
			SipApplicationSession appSession, User user,
			UserVoipAccount account, String phoneNumber) throws IOException {
		GoogleVoiceSession gvSession = googleVoiceManager
				.getGoogleVoiceSession(account.getAccount(),
						account.getPassword(), account.getCallBackNumber());
		appSession.setAttribute(GV_SESSION, gvSession);
		try {
			String pn = phoneNumberUtil.getCorrectUsCaPhoneNumber(phoneNumber,
					user.getDefaultArea());
			gvSession.login();
			if (gvSession.call(pn, "1")) {
				if (logger.isInfoEnabled()) {
					logger.info("{} is calling {} by google voice",
							user.getDisplayName(), pn);
				}
				appSession
						.setAttribute(GV_WAITING_FOR_CALLBACK, phoneNumberUtil
								.getCorrectUsCaPhoneNumber(
										account.getPhoneNumber(),
										user.getDefaultArea()));
				SipSession session = req.getSession();
				session.setAttribute(ORIGINAL_REQUEST, req);
				appSession.setAttribute(ORIGINAL_SESSION, session);
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("Google voice call failed.");
				}
				responseError(req, SipServletResponse.SC_DECLINE,
						"Google voice call failed.");
				return;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info(
						"Error happened during google voice call. Error message: {}",
						e.getMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Exception stack: ", e);
				}
			}
			responseError(req, SipServletResponse.SC_BAD_GATEWAY);
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.servlet.B2bServlet#doCancel(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doCancel(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got CANCEL: ", req);
		}
		URI fromUri = req.getFrom().getURI();
		if (fromUri.isSipURI()) {
			String appSessionIdKey = generateAppSessionKey(req, true);
			String appSessionId = (String) getServletContext().getAttribute(
					appSessionIdKey);
			if (appSessionId != null) {
				getServletContext().removeAttribute(appSessionIdKey);
				SipApplicationSession appSession = sipSessionsUtil
						.getApplicationSessionById(appSessionId);
				if (appSession != null) {
					GoogleVoiceSession gvSession = (GoogleVoiceSession) appSession
							.getAttribute(GV_SESSION);
					if (gvSession != null) {
						try {
							if (gvSession.cancel()) {
								if (logger.isDebugEnabled()) {
									logger.debug("Google voice call had been canceld");
								}
							} else {
								if (logger.isInfoEnabled()) {
									logger.info("Error happened when cancel google voice call.");
								}
							}
						} catch (Exception e) {
							if (logger.isWarnEnabled()) {
								logger.warn("Unable to cancel google voice call.");
							}
						}
					}
					appSession.invalidate();
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Cannot found appSession by id: {}",
								appSessionId);
					}
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Cannot found appSessionId from context by: {}",
							appSessionIdKey);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.servlet.B2bServlet#getB2buaHelper(javax.servlet.sip.
	 * SipServletRequest)
	 */
	@Override
	protected B2buaHelper getB2buaHelper(SipServletRequest req) {
		if (req instanceof SipServletRequestImpl) {
			final SipServletRequestImpl request = (SipServletRequestImpl) req;
			final MobicentsSipSession session = request.getSipSession();
			if (session.getProxy() != null)
				throw new IllegalStateException("Proxy already present");
			B2buaHelperImpl b2buaHelper = session.getB2buaHelper();
			if (b2buaHelper != null && b2buaHelper instanceof GvB2buaHelperImpl)
				return b2buaHelper;
			if (b2buaHelper == null) {
				b2buaHelper = (B2buaHelperImpl) req.getB2buaHelper();
			}
			GvB2buaHelperImpl helper = new GvB2buaHelperImpl(b2buaHelper);
			session.setB2buaHelper(b2buaHelper);
			return helper;
		} else {
			return req.getB2buaHelper();
		}
	}
}
