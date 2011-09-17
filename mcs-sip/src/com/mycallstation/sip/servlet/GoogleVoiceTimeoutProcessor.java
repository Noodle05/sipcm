/**
 * 
 */
package com.mycallstation.sip.servlet;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.googlevoice.GoogleVoiceSession;

/**
 * @author wgao
 * 
 */
public class GoogleVoiceTimeoutProcessor implements TimerProcessor {
	private static final long serialVersionUID = 2297331603087086579L;

	private static final Logger logger = LoggerFactory
			.getLogger(GoogleVoiceTimeoutProcessor.class);

	private final UserSipProfile user;
	private final SipServletRequest req;

	public GoogleVoiceTimeoutProcessor(SipServletRequest req,
			UserSipProfile user) {
		this.req = req;
		this.user = user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.servlet.TimerProcessor#timeout(javax.servlet.sip
	 * .ServletTimer)
	 */
	@Override
	public void timeout(ServletTimer timer) {
		if (logger.isDebugEnabled()) {
			logger.debug("Timeout, google voice didn't go though in time, cancel it.");
		}
		if (req == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("Google voice timeout, but request object is null?");
			}
			return;
		}
		SipApplicationSession appSession = timer.getApplicationSession();
		if (appSession.getSessions().hasNext()) {
			String appSessionIdKey = AbstractSipServlet
					.generateAppSessionKey(user);
			SipSession session = (SipSession) appSession.getSessions().next();
			session.getServletContext().removeAttribute(appSessionIdKey);
		}
		GoogleVoiceSession gvSession = (GoogleVoiceSession) appSession
				.getAttribute(GoogleVoiceServlet.GV_SESSION);
		if (gvSession != null) {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Cancelling google voice call.");
				}
				gvSession.cancel();
				if (logger.isTraceEnabled()) {
					logger.trace("Google voice call canceled.");
				}
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Error happened when cancel google voice call.", e);
				}
			} finally {
				gvSession.logout();
			}
		}
		if (State.INITIAL.equals(req.getSession().getState())
				|| State.EARLY.equals(req.getSession().getState())) {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Response timeout to original INVITE request.");
				}
				SipServletResponse response = req
						.createResponse(SipServletResponse.SC_REQUEST_TIMEOUT);
				if (logger.isDebugEnabled()) {
					logger.debug("Sending response: {}", response);
				}
				response.send();
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Error happened when sendig timeout response to original request.");
				}
			}
		}
	}
}
