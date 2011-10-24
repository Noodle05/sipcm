/**
 * 
 */
package com.mycallstation.sip.keepalive;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.business.UserVoipAccountService;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.googlevoice.GoogleVoiceManager;
import com.mycallstation.googlevoice.GoogleVoiceSession;
import com.mycallstation.sip.util.SipConfiguration;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
@Component("phoneNumberKeepAlive")
public class PhoneNumberKeepAlive {
	private static final Logger logger = LoggerFactory
			.getLogger(PhoneNumberKeepAlive.class);

	@Resource(name = "globalScheduler")
	private TaskScheduler scheduler;

	@Resource(name = "globalExecutor")
	private AsyncTaskExecutor executor;

	@Resource(name = "systemConfiguration")
	private SipConfiguration appConfig;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "userVoipAccountService")
	private UserVoipAccountService voipAccountService;

	@Resource(name = "googleVoiceManager")
	private GoogleVoiceManager googleVoiceManager;

	private final Runnable task;
	private ScheduledFuture<?> taskFuture;
	private final Collection<Runnable> workers;
	private final Collection<Future<?>> workerFutures;
	private final Map<UserSipProfile, GVSessionTimer> pingingUsers;
	private final Lock pingingUserLock;

	private BlockingQueue<UserSipProfile> pingUsers;
	private final AtomicBoolean working;

	public PhoneNumberKeepAlive() {
		task = new KeepAliveTask();
		working = new AtomicBoolean(false);
		pingingUsers = new HashMap<UserSipProfile, GVSessionTimer>();
		pingingUserLock = new ReentrantLock();
		workers = new ArrayList<Runnable>();
		workerFutures = new ArrayList<Future<?>>();
	}

	@PostConstruct
	public void init() {
		long interval = appConfig.getKeepAliveTaskInterval();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, (int) interval);
		taskFuture = scheduler.scheduleWithFixedDelay(task, c.getTime(),
				interval);
		int concurrency = appConfig.getKeepAliveConcurrency();
		for (int i = 0; i < concurrency; i++) {
			workers.add(new KeepAliveWorker());
		}
		pingUsers = new LinkedBlockingQueue<UserSipProfile>(
				appConfig.getGlobalBatchSize());
	}

	@PreDestroy
	public void destroy() {
		if (taskFuture != null) {
			taskFuture.cancel(false);
			taskFuture = null;
		}
	}

	public void ping() throws InterruptedException {
		if (working.compareAndSet(false, true)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Start phone number keep alive ping task.");
			}
			try {
				// Avoid load all objects into memory, load ID only.
				Collection<Long> uspIds = userSipProfileService
						.getNeedPingUserSipProfile(
								appConfig.getKeepAliveTimeout(),
								appConfig.isKeepAliveOnlineOnly());
				if (uspIds != null && !uspIds.isEmpty()) {
					if (uspIds.size() == 1) {
						if (logger.isTraceEnabled()) {
							logger.trace("Only one user need to ping, we will just use this thread to ping.");
						}
						// If only one records, don't use multi-threading
						Long id = uspIds.iterator().next();
						UserSipProfile usp = userSipProfileService
								.getEntityById(id);
						if (usp != null) {
							try {
								ping(usp);
							} catch (Throwable e) {
								if (logger.isWarnEnabled()) {
									logger.warn(
											"Error happened when ping for user: "
													+ usp, e);
								}
							}
						} else {
							if (logger.isWarnEnabled()) {
								logger.warn("Cannot found user by id: {}", id);
							}
						}
					} else {
						// Use maximum number of workers threads, if records
						// less than workers, use number of records threads
						// otherwise
						Iterator<Runnable> ite = workers.iterator();
						int num = Math.min(uspIds.size(), workers.size());
						for (int i = 0; i < num; i++) {
							workerFutures.add(executor.submit(ite.next()));
						}
						for (Long id : uspIds) {
							UserSipProfile usp = userSipProfileService
									.getEntityById(id);
							if (usp != null) {
								if (logger.isTraceEnabled()) {
									logger.trace("Adding user {} into query.",
											usp);
								}
								pingUsers.put(usp);
							} else {
								if (logger.isWarnEnabled()) {
									logger.warn("Cannot found user by id: {}",
											id);
								}
							}
						}
						if (logger.isTraceEnabled()) {
							logger.trace("All user been added into the query, now add ending user.");
						}
						for (int i = 0; i < workerFutures.size(); i++) {
							pingUsers.put(new EndUserSipProfile());
						}
						if (logger.isTraceEnabled()) {
							logger.trace("Now waiting for all ping thread done.");
						}
						for (Future<?> f : workerFutures) {
							try {
								f.get();
							} catch (Throwable e) {
								if (logger.isWarnEnabled()) {
									logger.warn(
											"Error happened in ping thread.", e);
								}
							}
						}
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Ping done. Exit");
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Didn't find any user to ping. Exit.");
					}
				}
			} finally {
				pingUsers.clear();
				workerFutures.clear();
				working.set(false);
			}
		}
	}

	private void loopPing() throws InterruptedException {
		if (logger.isDebugEnabled()) {
			logger.debug("Ping worker thread start.");
		}
		boolean done = false;
		while (!done) {
			UserSipProfile usp = pingUsers.take();
			if (usp instanceof EndUserSipProfile) {
				done = true;
			} else {
				try {
					ping(usp);
				} catch (Throwable e) {
					if (logger.isWarnEnabled()) {
						logger.warn("Error happened when ping for user " + usp
								+ ", continue to next.", e);
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Ping worker thread end.");
		}
	}

	private void ping(UserSipProfile userSipProfile) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("Start ping for user {}", userSipProfile);
		}
		// Add this user into current pinging user collection
		UserVoipAccount gvAccount = voipAccountService
				.getUserGoogleVoiceAccount(userSipProfile);
		if (gvAccount != null) {
			GoogleVoiceSession gvSession = googleVoiceManager
					.getGoogleVoiceSession(gvAccount.getAccount(),
							gvAccount.getPassword(),
							gvAccount.getCallBackNumber());
			gvSession.login();
			String type = gvAccount.getCallBackType() == null ? "1" : gvAccount
					.getCallBackType().toString();
			if (gvSession.call(gvAccount.getPhoneNumber(), type)) {
				if (logger.isTraceEnabled()) {
					logger.trace("Google voice call request sent, now add it to pinging user list.");
				}
				GVSessionTimer gvTimer = new GVSessionTimer(gvAccount,
						gvSession);
				pingingUserLock.lock();
				try {
					pingingUsers.put(userSipProfile, gvTimer);
				} finally {
					pingingUserLock.unlock();
				}
				gvTimer.waitingForDone();
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("Google voice call failed for user {}.",
							userSipProfile);
				}
				gvSession.logout();
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(
						"User {} has no google voice account, will not ping.",
						userSipProfile);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Ping for user {} done.", userSipProfile);
		}
	}

	public boolean removePingingUser(UserSipProfile userSipProfile) {
		if (userSipProfile == null) {
			throw new NullPointerException("Cannot remove null.");
		}
		pingingUserLock.lock();
		try {
			if (logger.isTraceEnabled()) {
				logger.trace("Removing user from pinging user list. {}",
						userSipProfile);
			}
			GVSessionTimer gvst = pingingUsers.remove(userSipProfile);
			if (gvst != null) {
				gvst.cancelTimer();
				return true;
			}
		} finally {
			pingingUserLock.unlock();
		}
		return false;
	}

	public boolean receiveCall(UserSipProfile user, String from) {
		pingingUserLock.lock();
		try {
			GVSessionTimer gvst = pingingUsers.get(user);
			if (gvst != null) {
				if (PhoneNumberUtil.getCanonicalizedPhoneNumber(from).equals(
						gvst.getGVPhoneNumber())) {
					if (logger.isTraceEnabled()) {
						logger.trace("This from number is user's google voice phone number, it's ping back.");
					}
					gvst.receivedCall();
					return true;
				}
			}
		} finally {
			pingingUserLock.unlock();
		}
		return false;
	}

	private class KeepAliveTask implements Runnable {
		@Override
		public void run() {
			try {
				ping();
			} catch (Throwable e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Error happened when execute ping.", e);
				}
			}
		}
	}

	private class KeepAliveWorker implements Runnable {
		@Override
		public void run() {
			try {
				loopPing();
			} catch (Throwable e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Error happened when loop though pinging user.", e);
				}
			}
		}
	}

	private class GVSessionTimer implements Runnable {
		private final UserVoipAccount account;
		private final GoogleVoiceSession session;
		private final ScheduledFuture<?> future;
		private volatile boolean received;
		private final CountDownLatch doneSignal;

		private GVSessionTimer(UserVoipAccount account,
				GoogleVoiceSession session) {
			assert account != null;
			assert session != null;
			this.account = account;
			this.session = session;
			Calendar c = Calendar.getInstance();
			c.add(Calendar.SECOND, appConfig.getKeepAliveGoogleVoiceTimeout());
			future = scheduler.schedule(this, c.getTime());
			received = false;
			doneSignal = new CountDownLatch(1);
		}

		public void receivedCall() {
			received = true;
		}

		public void run() {
			try {
				if (!received) {
					if (logger.isInfoEnabled()) {
						logger.info(
								"Didn't get response for user {} in time, cancel the call.",
								account.getOwner());
					}
					session.cancel();
				} else {
					if (logger.isInfoEnabled()) {
						logger.trace(
								"Ping get response, however didn't go though properly. User: {}.",
								account.getOwner());
					}
				}
			} catch (Throwable e) {
				if (logger.isInfoEnabled()) {
					logger.info(
							"Error happened when logout google voice session.",
							e);
				}
			} finally {
				session.logout();
				removePingingUser(account.getOwner());
				doneSignal.countDown();
			}
		}

		public void cancelTimer() {
			try {
				if (!future.isDone() && !future.isCancelled()) {
					if (future.cancel(true)) {
						session.logout();
					}
				}
			} finally {
				doneSignal.countDown();
			}
		}

		public void waitingForDone() throws InterruptedException {
			doneSignal.await();
		}

		public String getGVPhoneNumber() {
			return account.getPhoneNumber();
		}
	}

	private static class EndUserSipProfile extends UserSipProfile {
		private static final long serialVersionUID = 4297896283222338399L;
	}
}
