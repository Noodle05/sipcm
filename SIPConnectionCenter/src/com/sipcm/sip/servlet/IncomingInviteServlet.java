/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Configurable;

import com.sipcm.sip.model.AddressBinding;

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
		if (phoneNumberUtil.isValidPhoneNumber(fromUser)) {
			String appSessionId = (String) getServletContext().getAttribute(
					generateAppSessionKey(req, false));
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
			@SuppressWarnings("unchecked")
			Collection<AddressBinding> bindings = (Collection<AddressBinding>) req
					.getAttribute(TARGET_USERSIPBINDING);
			if (bindings == null || bindings.isEmpty()) {
				response(req, SipServletResponse.SC_NOT_FOUND);
				return;
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Forward to back-to-back servlet.");
			}
			dispatcher = req.getRequestDispatcher("B2bServlet");
		}
		dispatcher.forward(req, null);
	}
}
