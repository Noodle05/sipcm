package com.mycallstation.external.ipkall;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.junit.Ignore;
import org.junit.Test;

import com.mycallstation.common.TestConfiguration;
import com.mycallstation.email.receiver.MailSession;
import com.mycallstation.email.receiver.MessageOperation;
import com.mycallstation.external.recaptcha.GoogleRecaptchaManagerTest;

public class IPKallMailTest {
	public static Pattern PHONE_NUMBER_PATTERN = Pattern
			.compile(
					"^Thank you for signing up\\. Your IPKall phone number is: ([^.]+)\\.$",
					Pattern.MULTILINE);
	public static Pattern PASSWORD_PATTERN = Pattern.compile(
			"^Password: (.*)$", Pattern.MULTILINE);

	@Ignore
	@Test
	public void test() throws MessagingException, IOException {
		MailSession session = new MailSession();
		session.setAccount("wei@gaofamily.org");
		session.setPassword("Jack_519");
		final Collection<IPKallSession> ipKallSessions = new HashSet<IPKallSession>();
		session.searchEmailBySenderAndSubjet("info@ipkall.com",
				"Here is your IPKall phone number", new MessageOperation() {
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
								IPKallSession s = new IPKallSession();
								s.appConfig = new TestConfiguration();
								s.clientConntectionManager = new ThreadSafeClientConnManager();
								s.googleRecaptchaManager = new GoogleRecaptchaManagerTest();
								s.init();
								s.setPhoneNumber(phoneNumber);
								s.setPassword(password);
								ipKallSessions.add(s);
							}
						}
					}
				});
		Iterator<IPKallSession> ite = ipKallSessions.iterator();
		while (ite.hasNext()) {
			IPKallSession s = ite.next();
			try {
				try {
					s.login();
				} catch (Throwable e) {
					ite.remove();
				}
			} catch (Exception e) {
				e.printStackTrace();
				ite.remove();
			} finally {
				s.logout();
			}
		}
		assertNotNull(ipKallSessions);
	}

	@Test
	public void testNewMessage() throws MessagingException, IOException,
			InterruptedException {
		final MailSession session = new MailSession();
		session.setAccount("wei@gaofamily.org");
		session.setPassword("Jack_519");
		final Collection<IPKallSession> ipKallSessions = new HashSet<IPKallSession>();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					session.waitingForNewEmailBySenderAndSubject("INBOX",
							"info@ipkall.com",
							"Here is your IPKall phone number",
							new MessageOperation() {
								@Override
								public void execute(Message[] messages)
										throws MessagingException, IOException {
									System.out.println("Processing message");
									Matcher pnMatcher = PHONE_NUMBER_PATTERN
											.matcher("");
									Matcher passwordMatcher = PASSWORD_PATTERN
											.matcher("");
									for (Message msg : messages) {
										String phoneNumber = null;
										String password = null;
										Object content = msg.getContent();
										if (content instanceof String) {
											String str = (String) content;
											pnMatcher.reset(str);
											if (pnMatcher.find()) {
												phoneNumber = pnMatcher
														.group(1);
											}
											passwordMatcher.reset(str);
											if (passwordMatcher.find()) {
												password = passwordMatcher
														.group(1);
											}
										}
										if (phoneNumber != null
												&& password != null) {
											IPKallSession s = new IPKallSession();
											s.appConfig = new TestConfiguration();
											s.clientConntectionManager = new ThreadSafeClientConnManager();
											s.googleRecaptchaManager = new GoogleRecaptchaManagerTest();
											s.init();
											s.setPhoneNumber(phoneNumber);
											s.setPassword(password);
											synchronized (ipKallSessions) {
												ipKallSessions.add(s);
												ipKallSessions.notifyAll();
											}
										}
									}
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		synchronized (ipKallSessions) {
			while (ipKallSessions.isEmpty()) {
				ipKallSessions.wait();
			}
		}
		session.stopWaiting();
		t.join();
		Iterator<IPKallSession> ite = ipKallSessions.iterator();
		while (ite.hasNext()) {
			IPKallSession s = ite.next();
			try {
				try {
					s.login();
					s.cancel();
				} catch (Throwable e) {
					ite.remove();
				}
			} catch (Exception e) {
				e.printStackTrace();
				ite.remove();
			} finally {
				try {
					s.logout();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		assertTrue(ipKallSessions.size() == 1);
		for (IPKallSession s : ipKallSessions) {
			try {
				s.login();
				fail();
			} catch (Exception e) {

			}
		}
	}
}
