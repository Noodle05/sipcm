/**
 * 
 */
package com.sipcm.sip.events;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sip.CallLogSaveTask")
public class CallLogSaveTask implements Runnable {
	private static final Logger logger = LoggerFactory
			.getLogger(CallLogSaveTask.class);

	@Resource(name = "sip.CallLogRecorder")
	private CallLogRecorder callLogRecorder;

	private final AtomicBoolean running;

	public CallLogSaveTask() {
		running = new AtomicBoolean(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	@Async
	public void run() {
		if (running.compareAndSet(false, true)) {
			try {
				callLogRecorder.saveCallLogs();
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error happened when save call logs.", e);
				}
			} finally {
				running.set(false);
			}
		}
	}
}
