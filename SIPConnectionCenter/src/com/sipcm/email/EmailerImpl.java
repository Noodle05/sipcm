/**
 * 
 */
package com.sipcm.email;

import java.util.concurrent.atomic.AtomicBoolean;

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

	private final AtomicBoolean emailSendEnabled = new AtomicBoolean(true);

	@Resource(name = "emailProcessor")
	private EmailProcessor processor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#postEmail(com.sipcm.email.EmailBean)
	 */
	@Override
	public boolean sendMail(EmailBean emailBean) {
		if (emailSendEnabled.get()) {
			if (processor.addEmail(emailBean)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Email bean \"{}\" added into send list",
							emailBean);
				}
				processor.process();
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#enableEmailService()
	 */
	@Override
	public void enableEmailService() {
		if (emailSendEnabled.compareAndSet(false, true)) {
			if (logger.isInfoEnabled()) {
				logger.info("Email Service enabled");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.Emailer#disableEmailService()
	 */
	@Override
	public void disableEmailService() {
		if (emailSendEnabled.compareAndSet(true, false)) {
			if (logger.isInfoEnabled()) {
				logger.info("Email Service disabled");
			}
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
