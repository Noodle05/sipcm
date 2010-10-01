/**
 * 
 */
package com.sipcm.process;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * @author Jack
 * 
 */
@Component("global.threadFactory")
public class ThreadFactory implements java.util.concurrent.ThreadFactory {
	final ThreadGroup group;

	final AtomicInteger threadNumber = new AtomicInteger(1);

	final String namePrefix;

	public ThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		ThreadGroup parentGroup = (s != null) ? s.getThreadGroup() : Thread
				.currentThread().getThreadGroup();
		group = new ThreadGroup(parentGroup, "Gaofamily");
		namePrefix = "ThreadPool-thread-";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix
				+ threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
