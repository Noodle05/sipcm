/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Configurable;

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
		SipURI toUri = (SipURI) req.getTo().getURI();
		SipURI fromUri = (SipURI) req.getFrom().getURI();
		String toUser = toUri.getUser();
		String fromUser = fromUri.getUser();
		if (PHONE_NUMBER.matcher(fromUser).matches()) {
			String appSessionId = (String) getServletContext().getAttribute(
					APPLICATION_SESSION_ID + toUser);
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
					dispatcher = req.getRequestDispatcher("GoogleVoiceServlet");
				} else {
					if (logger.isTraceEnabled()) {
						logger.trace("However call back number doesn't match.");
					}
				}
			}
		}
		if (dispatcher == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Forward to back-to-back servlet.");
			}
			dispatcher = req.getRequestDispatcher("B2bServlet");
		}
		dispatcher.forward(req, null);
	}
}
