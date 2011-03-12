/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("registerClientRenewTask")
public class RegisterClientRenewTask {
	private static final Logger logger = LoggerFactory
			.getLogger(RegisterClientRenewTask.class);

	private final AtomicBoolean running;

	public RegisterClientRenewTask() {
		running = new AtomicBoolean(false);
	}

	@Resource(name = "voipVendorManager")
	private VoipVendorManager voipVendorManager;

	@Scheduled(fixedRate = 60000L)
	public void onCheckExpire() {
		if (running.compareAndSet(false, true)) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Timeout, check contacts expire.");
				}
				voipVendorManager.registerClientRenew();
			} finally {
				running.set(false);
			}
		}
	}
}
