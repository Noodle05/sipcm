/**
 * 
 */
package com.sipcm.email;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Jack
 * 
 */
@Component("globalEmailProcessor")
public class EmailProcessorImpl implements EmailProcessor {
	private static final Logger logger = LoggerFactory
			.getLogger(EmailProcessorImpl.class);

	@Resource(name = "globalEmailQueue")
	private BlockingQueue<EmailBean> emailList;

	@Resource(name = "globalEmailService")
	private EmailService emailService;

	private final AtomicBoolean running;

	private final AtomicLong totalProceed;

	private final AtomicLong totalSucceed;

	private final AtomicLong totalFailed;

	public EmailProcessorImpl() {
		totalProceed = new AtomicLong(0L);
		totalSucceed = new AtomicLong(0L);
		totalFailed = new AtomicLong(0L);
		running = new AtomicBoolean(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.EmailProcessor#process()
	 */
	@Override
	@Async
	public void process() {
		if (running.compareAndSet(false, true)) {
			try {
				EmailBean emailBean = null;
				while ((emailBean = emailList.poll()) != null) {
					try {
						emailService.sendEmail(emailBean);
						totalSucceed.incrementAndGet();
					} catch (Exception e) {
						totalFailed.incrementAndGet();
						if (logger.isErrorEnabled()) {
							logger.error(
									"Error happened when sending email: \""
											+ emailBean + "\"", e);
						}
					} finally {
						totalProceed.incrementAndGet();
					}
				}
			} finally {
				running.set(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.EmailProcessor#addEmail(com.sipcm.email.EmailBean)
	 */
	@Override
	public boolean addEmail(EmailBean _email) {
		return emailList.offer(_email);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.EmailProcessor#getTotalProceed()
	 */
	@Override
	public long getTotalProceed() {
		return totalProceed.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.EmailProcessor#getTotalSucceed()
	 */
	@Override
	public long getTotalSucceed() {
		return totalSucceed.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.EmailProcessor#getTotalFailed()
	 */
	@Override
	public long getTotalFailed() {
		return totalFailed.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.email.EmailProcessor#resetCounter()
	 */
	@Override
	public void resetCounter() {
		totalProceed.set(0);
		totalSucceed.set(0);
		totalFailed.set(0);
	}
}
