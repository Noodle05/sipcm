/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Configurable;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.sip.events.CallStartEvent;
import com.mycallstation.sip.locationservice.UserBindingInfo;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * @author Wei Gao
 * 
 */
@Configurable
@SipServlet(name = "IncomingInviteServlet", applicationName = "com.mycallstation.CallCenter", loadOnStartup = 1)
public class IncomingInviteServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 4938128598461987936L;

	private volatile String[] genericGVCallbackNumber;
	private volatile boolean gvCallbackNumberInitialized = false;

	private void initGVCallbackNumber() {
		if (!gvCallbackNumberInitialized) {
			synchronized (this) {
				if (!gvCallbackNumberInitialized) {
					genericGVCallbackNumber = appConfig
							.getGoogleVoiceGenericCallbackNumber();
					gvCallbackNumberInitialized = true;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doInvite(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("Processing incoming invite.");
		}
		RequestDispatcher dispatcher = null;
		SipURI fromUri = (SipURI) req.getFrom().getURI();
		String fromUser = fromUri.getUser();
		UserBindingInfo ubi = (UserBindingInfo) req
				.getAttribute(TARGET_USERSIPBINDING);
		if (ubi == null) {
			response(req, SipServletResponse.SC_NOT_FOUND);
			return;
		}
		if (PhoneNumberUtil.isValidPhoneNumber(fromUser)) {
			UserSipProfile userSipProfile = ubi.getBindings().iterator().next()
					.getUserSipProfile();
			String appSessionId = (String) getServletContext().getAttribute(
					AbstractSipServlet.generateAppSessionKey(userSipProfile));
			SipApplicationSession appSession = null;
			if (appSessionId != null) {
				appSession = sipSessionsUtil
						.getApplicationSessionById(appSessionId);
			}
			if (appSession != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Found application session for this to user, this probably a google voice call back.");
				}
				if (checkCallbackNumber(fromUser, appSession)) {
					if (logger.isTraceEnabled()) {
						logger.trace("Call back number match too, forward to google voice servlet.");
					}
					req.setAttribute(USER_ATTRIBUTE, userSipProfile);
					dispatcher = req.getRequestDispatcher("GoogleVoiceServlet");
				} else {
					if (logger.isTraceEnabled()) {
						logger.trace(
								"However call back number doesn't match. Waiting call back from {}, this call from {}",
								appSession
										.getAttribute(GV_WAITING_FOR_CALLBACK),
								PhoneNumberUtil
										.getCanonicalizedPhoneNumber(fromUser));
					}
				}
			}
		}
		if (dispatcher == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Forward to back-to-back servlet.");
			}
			dispatcher = req.getRequestDispatcher("B2bServlet");
			if (callEventListener != null) {
				CallStartEvent event;
				String fu = fromUser;
				if (req.getAttribute(USER_ATTRIBUTE) != null) {
					fu = ((UserSipProfile) req.getAttribute(USER_ATTRIBUTE))
							.getPhoneNumber();
				}
				if (PhoneNumberUtil.isValidPhoneNumber(fu)) {
					fu = PhoneNumberUtil.getCanonicalizedPhoneNumber(fu);
				}
				if (ubi.getAccount() != null) {
					event = new CallStartEvent(ubi.getAccount(), fu);
				} else {
					UserSipProfile usp = ubi.getBindings().iterator().next()
							.getUserSipProfile();
					event = new CallStartEvent(usp, fu);
				}
				req.getSession().setAttribute(INCOMING_CALL_START, event);
				callEventListener.incomingCallStart(event);
			}
		}
		dispatcher.forward(req, null);
	}

	private boolean checkCallbackNumber(String fromUser,
			SipApplicationSession appSession) {
		String pn = PhoneNumberUtil.getCanonicalizedPhoneNumber(fromUser);
		if (pn.equals(appSession.getAttribute(GV_WAITING_FOR_CALLBACK))) {
			return true;
		}
		initGVCallbackNumber();
		if (genericGVCallbackNumber != null) {
			if (Arrays.binarySearch(genericGVCallbackNumber, pn) >= 0) {
				return true;
			}
		}
		return false;
	}
}
