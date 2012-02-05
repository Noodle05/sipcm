/**
 * 
 */
package com.mycallstation.sip.servlet;

import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.annotation.SipListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wei Gao
 * 
 */
@SipListener
public class TimerListenerImpl implements TimerListener {
	private static final Logger logger = LoggerFactory
			.getLogger(TimerListenerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.TimerListener#timeout(javax.servlet.sip.ServletTimer)
	 */
	@Override
	public void timeout(ServletTimer timer) {
		if (timer.getInfo() != null
				&& timer.getInfo() instanceof TimerProcessor) {
			TimerProcessor processor = (TimerProcessor) timer.getInfo();
			processor.timeout(timer);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Timeout, but cannot found info on timer.");
			}
		}
	}
}
