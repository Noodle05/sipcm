/**
 * 
 */
package com.sipcm.email;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Wei Gao
 * 
 */
@Component("emailer")
public class EmailerImpl implements Emailer {
	private static final Logger logger = LoggerFactory
			.getLogger(EmailerImpl.class);

	private boolean emailSendEnabled = true;

	@Resource(name = "emailProcessor")
	private EmailProcessor processor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#postEmail(com.sipcm.email.EmailBean)
	 */
	@Override
	public boolean postEmail(EmailBean emailBean) {
		if (!emailSendEnabled) {
			return false;
		}

		processor.addEmail(emailBean);
		if (logger.isDebugEnabled()) {
			logger.debug("Email bean \"{}\" added into send list", emailBean);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#enableEmailService()
	 */
	@Override
	public void enableEmailService() {
		if (!emailSendEnabled) {
			if (logger.isInfoEnabled()) {
				logger.info("Email Service enabled");
			}
			emailSendEnabled = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#disableEmailService()
	 */
	@Override
	public void disableEmailService() {
		if (emailSendEnabled) {
			if (logger.isInfoEnabled()) {
				logger.info("Email Service disabled");
			}
			emailSendEnabled = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#startup()
	 */
	@Override
	public void startup() {
		try {
			processor.startup();
			if (logger.isInfoEnabled()) {
				logger.info("Email service started");
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Email service cannot start", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#shutdown()
	 */
	@Override
	public void shutdown() {
		processor.shutdown();

		if (logger.isInfoEnabled()) {
			logger.info("Email service stopped");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#getTotalProceed()
	 */
	@Override
	public long getTotalProceed() {
		return processor.getTotalProceed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#getTotalSucceed()
	 */
	@Override
	public long getTotalSucceed() {
		return processor.getTotalSucceed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#getTotalFailed()
	 */
	@Override
	public long getTotalFailed() {
		return processor.getTotalFailed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#resetCounter()
	 */
	@Override
	public void resetCounter() {
		processor.resetCounter();
	}
}
