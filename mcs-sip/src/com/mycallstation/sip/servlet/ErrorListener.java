/**
 * 
 */
package com.mycallstation.sip.servlet;

import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wgao
 * 
 */
// @Configurable
// @SipListener(applicationName = "com.mycallstation.CallCenter")
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
		SipServletRequest req = ee.getRequest();
		SipServletResponse res = ee.getResponse();
		if (logger.isWarnEnabled()) {
			logger.warn(
					"ACK is not received for original request: \"{}\", response: \"{}\"",
					req, res);
		}
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
		SipServletRequest req = ee.getRequest();
		SipServletResponse res = ee.getResponse();
		if (logger.isWarnEnabled()) {
			logger.warn(
					"PRACK is not received for original request: \"{}\", response: \"{}\"",
					req, res);
		}
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
