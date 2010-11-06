/**
 * 
 */
package com.sipcm.sip.servlet;

import java.util.Iterator;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;
import javax.servlet.sip.annotation.SipListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wgao
 * 
 */
@SipListener
public class SipApplicationSessionListenerImpl implements
		SipApplicationSessionListener {
	private static final Logger logger = LoggerFactory
			.getLogger(SipApplicationSessionListenerImpl.class);

	private int extensionTime = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipApplicationSessionListener#sessionCreated(javax.
	 * servlet.sip.SipApplicationSessionEvent)
	 */
	@Override
	public void sessionCreated(SipApplicationSessionEvent ev) {
		if (logger.isDebugEnabled()) {
			logger.debug("ApplicationSession been created. session id: {}", ev
					.getApplicationSession().getId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipApplicationSessionListener#sessionDestroyed(javax
	 * .servlet.sip.SipApplicationSessionEvent)
	 */
	@Override
	public void sessionDestroyed(SipApplicationSessionEvent ev) {
		if (logger.isDebugEnabled()) {
			logger.debug("ApplicationSession been destroyed. session id: {}",
					ev.getApplicationSession().getId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipApplicationSessionListener#sessionExpired(javax.
	 * servlet.sip.SipApplicationSessionEvent)
	 */
	@Override
	public void sessionExpired(SipApplicationSessionEvent ev) {
		if (logger.isDebugEnabled()) {
			logger.debug("ApplicationSession: {} is going to expired.", ev
					.getApplicationSession().getId());
		}
		SipApplicationSession appSession = ev.getApplicationSession();
		Iterator<?> ite = appSession.getSessions("SIP");
		boolean extendIt = false;
		if (logger.isTraceEnabled()) {
			logger.trace("Checking Sip Sessions ...");
		}
		while (ite.hasNext() && !extendIt) {
			Object o = ite.next();
			if (o instanceof SipSession) {
				final SipSession session = (SipSession) o;
				if (logger.isTraceEnabled()) {
					logger.trace(
							"SipSession is read to invalid? {}, session state: {}",
							session.isReadyToInvalidate(), session.getState());
				}
				if (!State.TERMINATED.equals(session.getState())
						&& !State.INITIAL.equals(session.getState())) {
					if (logger.isTraceEnabled()) {
						logger.trace(
								"SipSession: {} state is not TERMINATED and not INITIAL, extend appSession",
								session.getId());
					}
					extendIt = true;
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Checking Sip Sessions done. extendIt: {}", extendIt);
		}
		if (extendIt) {
			if (logger.isDebugEnabled()) {
				logger.debug("Extend application session: {} by 3 minutes.",
						appSession.getId());
			}
			appSession.setExpires(extensionTime);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipApplicationSessionListener#sessionReadyToInvalidate
	 * (javax.servlet.sip.SipApplicationSessionEvent)
	 */
	@Override
	public void sessionReadyToInvalidate(SipApplicationSessionEvent ev) {
		if (logger.isDebugEnabled()) {
			logger.debug("ApplicationSession: {} is ready to invalidate.", ev
					.getApplicationSession().getId());
		}
	}
}
