/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.sip.message.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wei Gao
 * 
 */
public class KeepAliveChatTimeoutProcessor implements TimerProcessor {
	private static final long serialVersionUID = -8810949594720527781L;

	private static final Logger logger = LoggerFactory
			.getLogger(KeepAliveChatTimeoutProcessor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.servlet.TimerProcessor#timeout(javax.servlet.sip
	 * .ServletTimer)
	 */
	@Override
	public void timeout(ServletTimer timer) {
		if (logger.isTraceEnabled()) {
			logger.trace("Timeout, prepare to send bye.");
		}
		SipApplicationSession appSession = timer.getApplicationSession();
		Iterator<?> ite = appSession.getSessions();
		if (ite.hasNext()) {
			SipSession session = (SipSession) appSession.getSessions().next();
			SipServletRequest req = session.createRequest(Request.BYE);
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Sending bye request. {}", req);
				}
				req.send();
			} catch (IOException e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Error happened when send bye request.", e);
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Application Session contains no SipSession?");
			}
		}
	}
}
