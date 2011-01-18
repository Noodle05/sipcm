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
@Component("emailProcessor")
public class EmailProcessor {
	private static final Logger logger = LoggerFactory
			.getLogger(EmailProcessor.class);

	@Resource(name = "email.queue")
	private BlockingQueue<EmailBean> emailList;

	@Resource(name = "emailService")
	private EmailService emailService;

	private final AtomicBoolean working;

	private final AtomicBoolean running;

	private final AtomicLong totalProceed;

	private final AtomicLong totalSucceed;

	private final AtomicLong totalFailed;

	public EmailProcessor() {
		totalProceed = new AtomicLong(0L);
		totalSucceed = new AtomicLong(0L);
		totalFailed = new AtomicLong(0L);
		working = new AtomicBoolean(true);
		running = new AtomicBoolean(false);
	}

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

	void addEmail(EmailBean _email) {
		if (working.get()) {
			if (emailList.offer(_email)) {
				process();
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Queue is full, dropping email \"{}\"", _email);
				}
			}
		}
	}

	public void startup() {
		working.compareAndSet(false, true);
	}

	void shutdown() {
		working.compareAndSet(true, false);
	}

	/**
	 * @return total proceed email bean
	 */
	public long getTotalProceed() {
		return totalProceed.get();
	}

	/**
	 * @return total successfully proceed email bean
	 */
	public long getTotalSucceed() {
		return totalSucceed.get();
	}

	/**
	 * @return total failed proceed email bean
	 */
	public long getTotalFailed() {
		return totalFailed.get();
	}

	void resetCounter() {
		totalProceed.set(0);
		totalSucceed.set(0);
		totalFailed.set(0);
	}
}
