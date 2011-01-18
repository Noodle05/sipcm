/**
 * 
 */
package com.sipcm.sip.servlet;

import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.annotation.SipListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author wgao
 * 
 */
@Configurable
@SipListener(applicationName = "org.gaofamily.CallCenter")
public class ErrorListener implements SipErrorListener {
	public static final Logger logger = LoggerFactory
			.getLogger(ErrorListener.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.sip.SipErrorListener#noAckReceived(javax.servlet.sip.
	 * SipErrorEvent)
	 */
	@Override
	public void noAckReceived(SipErrorEvent ee) {
		if (logger.isErrorEnabled()) {
			logger.error("ACK is not received.");
		}
		SipServletRequest req = ee.getRequest();
		SipSession session = req.getSession(false);
		if (session != null) {
			B2buaHelper helper = req.getB2buaHelper();
			if (helper != null) {
				SipSession origSession = helper.getLinkedSession(session);
				if (origSession != null) {
					if (logger.isTraceEnabled()) {
						logger.trace(
								"Find original session {} for session {}.",
								origSession.getId(), session.getId());
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Invalidate original session {}.",
								origSession.getId());
					}
					origSession.invalidate();
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Invalidate session {}.", session.getId());
			}
			session.invalidate();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipErrorListener#noPrackReceived(javax.servlet.sip.
	 * SipErrorEvent)
	 */
	@Override
	public void noPrackReceived(SipErrorEvent ee) {
		if (logger.isErrorEnabled()) {
			logger.error("PRACK is not received.");
		}
		SipServletRequest req = ee.getRequest();
		SipSession session = req.getSession(false);
		if (session != null) {
			B2buaHelper helper = req.getB2buaHelper();
			if (helper != null) {
				SipSession origSession = helper.getLinkedSession(session);
				if (origSession != null) {
					if (logger.isTraceEnabled()) {
						logger.trace(
								"Find original session {} for session {}.",
								origSession.getId(), session.getId());
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Invalidate original session {}.",
								origSession.getId());
					}
					origSession.invalidate();
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Invalidate session {}.", session.getId());
			}
			session.invalidate();
		}
	}
}
