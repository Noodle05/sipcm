/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Configurable;

import com.sipcm.sip.events.CallStartEvent;
import com.sipcm.sip.locationservice.UserBindingInfo;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "IncomingInviteServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class IncomingInviteServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 4938128598461987936L;

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
		if (phoneNumberUtil.isValidPhoneNumber(fromUser)) {
			UserSipProfile userSipProfile = ubi.getBindings().iterator().next()
					.getUserSipProfile();
			String appSessionId = (String) getServletContext().getAttribute(
					generateAppSessionKey(userSipProfile));
			SipApplicationSession appSession = null;
			if (appSessionId != null) {
				appSession = sipSessionsUtil
						.getApplicationSessionById(appSessionId);
			}
			if (appSession != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Found application session for this to user, this probably a google voice call back.");
				}
				if (phoneNumberUtil.getCanonicalizedPhoneNumber(fromUser)
						.equals(appSession
								.getAttribute(GV_WAITING_FOR_CALLBACK))) {
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
								phoneNumberUtil
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
				if (phoneNumberUtil.isValidPhoneNumber(fu)) {
					fu = phoneNumberUtil.getCanonicalizedPhoneNumber(fu);
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
}
