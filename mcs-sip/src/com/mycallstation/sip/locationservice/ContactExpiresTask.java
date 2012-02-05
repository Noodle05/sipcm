/**
 * 
 */
package com.mycallstation.sip.locationservice;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Wei Gao
 * 
 */
@Component("sipContactExpiresTask")
public class ContactExpiresTask {
	private static final Logger logger = LoggerFactory
			.getLogger(ContactExpiresTask.class);

	private final AtomicBoolean running;

	public ContactExpiresTask() {
		running = new AtomicBoolean(false);
	}

	@Resource(name = "sipLocationService")
	private LocationService locationService;

	@Scheduled(fixedRate = 300000L)
	public void onCheckExpire() {
		if (running.compareAndSet(false, true)) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Timeout, check contacts expire.");
				}
				locationService.checkContactExpires();
			} finally {
				running.set(false);
			}
		}
	}
}
