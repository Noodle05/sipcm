/**
 * 
 */
package com.sipcm.process;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.Resource;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Jack
 * 
 */
public class BackendScheduledThreadPool extends ScheduledThreadPoolExecutor {
	private static ThreadLocal<Boolean> participate = new ThreadLocal<Boolean>();

	@Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;

	public BackendScheduledThreadPool(int corePoolSize) {
		super(corePoolSize);
	}

	public BackendScheduledThreadPool(int corePoolSize,
			ThreadFactory threadFactory) {
		super(corePoolSize, threadFactory);
	}

	public BackendScheduledThreadPool(int corePoolSize,
			RejectedExecutionHandler handler) {
		super(corePoolSize, handler);
	}

	public BackendScheduledThreadPool(int corePoolSize,
			ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, threadFactory, handler);
	}

	/*
	 * Springframework is using FlushMode.NEVER, so we use it here also, even
	 * it's been deprecated
	 * 
	 * @see
	 * java.util.concurrent.ThreadPoolExecutor#beforeExecute(java.lang.Thread,
	 * java.lang.Runnable)
	 */
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		participate.set(false);
		if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
			// Do not modify the Session: just set the participate flag.
			participate.set(true);
		} else {
			Session session = SessionFactoryUtils.getSession(sessionFactory,
					true);
			session.setFlushMode(FlushMode.MANUAL);
			TransactionSynchronizationManager.bindResource(sessionFactory,
					new SessionHolder(session));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable,
	 * java.lang.Throwable)
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		// Close hibernate session.
		if (!participate.get()) {
			// single session mode
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager
					.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}
}
