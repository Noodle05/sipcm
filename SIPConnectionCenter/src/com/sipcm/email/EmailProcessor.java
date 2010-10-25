/**
 * 
 */
package com.sipcm.email;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.sipcm.process.SingleThreadProcessor;
import org.springframework.stereotype.Component;

/**
 * @author Jack
 * 
 */
@Component("emailProcessor")
public class EmailProcessor extends SingleThreadProcessor<Long> {
	private static enum Status {
		PROCESSING, NOT_PROCESSING;
	}

	@Resource(name = "email.queue")
	private BlockingQueue<EmailBean> emailList;

	@Resource(name = "emailService")
	private EmailService emailService;

	private AtomicReference<Status> status;

	private AtomicLong totalProceed;

	private AtomicLong totalSucceed;

	private AtomicLong totalFailed;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.process.SingleThreadProcessor#init()
	 */
	@PostConstruct
	@Override
	public void init() {
		super.init();
		totalProceed = new AtomicLong(0L);
		totalSucceed = new AtomicLong(0L);
		totalFailed = new AtomicLong(0L);
		status = new AtomicReference<Status>(Status.PROCESSING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.process.SingleThreadProcessor#internalRun()
	 */
	@Override
	protected Long internalRun() {
		EmailBean emailBean = null;
		while ((emailBean = emailList.poll()) != null) {
			try {
				emailService.sendEmail(emailBean);
				totalSucceed.incrementAndGet();
			} catch (Exception e) {
				totalFailed.incrementAndGet();
				if (logger.isErrorEnabled()) {
					logger.error("Error happened when sending email: \""
							+ emailBean + "\"", e);
				}
			} finally {
				totalProceed.incrementAndGet();
			}
		}
		return totalProceed.get();
	}

	void addEmail(EmailBean _email) {
		if (Status.PROCESSING.equals(status.get())) {
			if (emailList.offer(_email)) {
				start();
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Queue is full, dropping email \"{}\"", _email);
				}
			}
		}
	}

	public void startup() {
		status.compareAndSet(Status.NOT_PROCESSING, Status.PROCESSING);
	}

	void shutdown() {
		status.compareAndSet(Status.PROCESSING, Status.NOT_PROCESSING);
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
