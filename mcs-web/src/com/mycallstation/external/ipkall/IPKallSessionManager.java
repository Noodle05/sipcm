/**
 * 
 */
package com.mycallstation.external.ipkall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.mycallstation.email.receiver.MailSession;
import com.mycallstation.email.receiver.MessageOperation;

/**
 * @author wgao
 * 
 */
public abstract class IPKallSessionManager {
	private static final Logger logger = LoggerFactory
			.getLogger(IPKallSessionManager.class);

	public static Pattern PHONE_NUMBER_PATTERN = Pattern
			.compile(
					"^Thank you for signing up\\. Your IPKall phone number is: ([^.]+)\\.$",
					Pattern.MULTILINE);
	public static Pattern PASSWORD_PATTERN = Pattern.compile(
			"^Password: (.*)$", Pattern.MULTILINE);

	public static final String IPKALL_SENDER = "info@ipkall.com";

	public static final String IPKALL_SUBJECT = "Here is your IPKall phone number";

	protected abstract IPKallSession createIPKallSession();

	public Collection<IPKallSession> searchIPKallSessionFromEmail(
			MailSession mailSession, String... folders)
			throws MessagingException, IOException {
		if (mailSession == null) {
			throw new NullPointerException("mail session cannot be null.");
		}
		final Collection<IPKallSession> ipKallSessions = new HashSet<IPKallSession>();
		mailSession.searchEmailBySenderAndSubjet(IPKALL_SENDER, IPKALL_SUBJECT,
				new MessageOperation() {
					@Override
					public void execute(Message[] messages)
							throws MessagingException, IOException {
						Matcher pnMatcher = PHONE_NUMBER_PATTERN.matcher("");
						Matcher passwordMatcher = PASSWORD_PATTERN.matcher("");
						for (Message msg : messages) {
							String phoneNumber = null;
							String password = null;
							Object content = msg.getContent();
							if (content instanceof String) {
								String str = (String) content;
								pnMatcher.reset(str);
								if (pnMatcher.find()) {
									phoneNumber = pnMatcher.group(1);
								}
								passwordMatcher.reset(str);
								if (passwordMatcher.find()) {
									password = passwordMatcher.group(1);
								}
							}
							if (phoneNumber != null && password != null) {
								IPKallSession s = createIPKallSession();
								s.setPhoneNumber(phoneNumber);
								s.setPassword(password);
								ipKallSessions.add(s);
							} else {
								if (logger.isWarnEnabled()) {
									logger.warn(
											"Cannot find phone number or password from email message. Content: {}",
											content);
								}
							}
						}
					}
				}, folders);
		Iterator<IPKallSession> ite = ipKallSessions.iterator();
		while (ite.hasNext()) {
			IPKallSession s = ite.next();
			try {
				if (logger.isTraceEnabled()) {
					logger.trace(
							"Try to login ip kall session to verify. IPKall session: {}",
							s);
				}
				s.login();
			} catch (Throwable e) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Cannot login for ip kall session {}, probably already invalid, remove it.",
							s);
				}
				ite.remove();
			} finally {
				try {
					s.logout();
				} catch (Throwable e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Error happened when logout, ignore it.",
								e);
					}
				}
			}
		}
		return ipKallSessions;
	}

	public IPKallSession waitingNewIPKallSessionFromEmail(
			MailSession mailSession, String folder, long timeout, TimeUnit unit)
			throws MessagingException, InterruptedException,
			NoIPKallSessionFoundException {
		if (mailSession == null) {
			throw new NullPointerException("mail session cannot be null.");
		}
		if (folder == null) {
			throw new NullPointerException("folder cannot be null.");
		}
		if (timeout <= 0L) {
			throw new IllegalArgumentException(
					"Timeout value must be positive number.");
		}
		if (unit == null) {
			throw new NullPointerException("unit cannot be null.");
		}
		final Collection<IPKallSession> ipKallSessions = new ArrayList<IPKallSession>(
				1);
		CountDownLatch endSignal = new CountDownLatch(1);
		waiting(mailSession, folder, ipKallSessions, endSignal);
		try {
			if (endSignal.await(timeout, unit)) {
				if (!ipKallSessions.isEmpty()) {
					Iterator<IPKallSession> ite = ipKallSessions.iterator();
					while (ite.hasNext()) {
						IPKallSession s = ite.next();
						try {
							s.login();
						} catch (Throwable e) {
							if (logger.isWarnEnabled()) {
								logger.warn(
										"Cannot login for ip kall session {}, remove it.",
										s);
							}
							ite.remove();
						} finally {
							try {
								s.logout();
							} catch (Throwable e) {
								if (logger.isDebugEnabled()) {
									logger.debug(
											"Error happened when logout, ignore it.",
											e);
								}
							}
						}
					}
					if (!ipKallSessions.isEmpty()) {
						return ipKallSessions.iterator().next();
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("The ip kall info found is invalid.");
						}
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("It's return true, but didn't find any ip kall info?");
					}
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Timeout, not receiving ip kall info.");
				}
			}
			throw new NoIPKallSessionFoundException();
		} finally {
			mailSession.stopWaiting();
		}
	}

	@Async
	void waiting(final MailSession mailSession, String folderName,
			final Collection<IPKallSession> ipKallSessions,
			final CountDownLatch endSignal) throws MessagingException {
		mailSession.waitingForNewEmailBySenderAndSubject(folderName,
				IPKALL_SENDER, IPKALL_SUBJECT, new MessageOperation() {
					@Override
					public void execute(Message[] messages)
							throws MessagingException, IOException {
						Matcher pnMatcher = PHONE_NUMBER_PATTERN.matcher("");
						Matcher passwordMatcher = PASSWORD_PATTERN.matcher("");
						for (Message msg : messages) {
							String phoneNumber = null;
							String password = null;
							Object content = msg.getContent();
							if (content instanceof String) {
								String str = (String) content;
								pnMatcher.reset(str);
								if (pnMatcher.find()) {
									phoneNumber = pnMatcher.group(1);
								}
								passwordMatcher.reset(str);
								if (passwordMatcher.find()) {
									password = passwordMatcher.group(1);
								}
							}
							if (phoneNumber != null && password != null) {
								IPKallSession s = createIPKallSession();
								s.setPhoneNumber(phoneNumber);
								s.setPassword(password);
								ipKallSessions.add(s);
							} else {
								if (logger.isDebugEnabled()) {
									logger.debug("Cannot find phone number of password.");
								}
							}
						}
						if (!ipKallSessions.isEmpty()) {
							endSignal.countDown();
						}
					}
				});
	}
}
