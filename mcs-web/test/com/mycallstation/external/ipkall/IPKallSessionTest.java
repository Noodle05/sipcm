/**
 * 
 */
package com.mycallstation.external.ipkall;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.junit.Ignore;
import org.junit.Test;

import com.mycallstation.common.TestConfiguration;
import com.mycallstation.external.recaptcha.GoogleRecaptchaManagerTest;
import com.mycallstation.external.recaptcha.RecaptchaFailedException;
import com.mycallstation.googlevoice.HttpResponseException;

/**
 * @author Wei Gao
 * 
 */
public class IPKallSessionTest {

	@Test
	public void test() {
		IPKallSession session = new IPKallSession();
		session.appConfig = new TestConfiguration();
		session.clientConntectionManager = new ThreadSafeClientConnManager();
		session.googleRecaptchaManager = new GoogleRecaptchaManagerTest();
		session.init();
		assertFalse(IPKallSession.availableAreaCode.isEmpty());
	}

	@Test(expected = RecaptchaFailedException.class)
	public void verificationFailedTest() throws IOException,
			RecaptchaFailedException {
		InputStream in = getClass().getResourceAsStream(
				"register_verification_failed.html");
		try {
			checkSuccess(in);
			fail();
		} finally {
			in.close();
		}
	}

	@Test
	public void verificationSuccessTest() throws IOException,
			RecaptchaFailedException {
		InputStream in = getClass()
				.getResourceAsStream("register_success.html");
		try {
			boolean success = checkSuccess(in);
			assertTrue(success);
		} finally {
			in.close();
		}
	}

	@Ignore
	@Test
	public void testLoginLogout() throws ClientProtocolException, IOException,
			HttpResponseException, IPKallException {
		IPKallSession session = new IPKallSession();
		session.appConfig = new TestConfiguration();
		session.clientConntectionManager = new ThreadSafeClientConnManager();
		session.googleRecaptchaManager = new GoogleRecaptchaManagerTest();
		session.init();
		session.setPhoneNumber("somenumber");
		session.setPassword("something");
		session.login();
		assertEquals("SIP", session.getAccountType());
		assertEquals("wlifeng", session.getSipPhoneNumber());
		assertEquals("wlifeng@yahoo.com", session.getEmail());
		assertEquals(120, session.getRingSeconds());
		session.setSipPhoneNumber("someuser1");
		session.update();
		assertEquals("someuser1", session.getSipPhoneNumber());
		session.setSipPhoneNumber("aaa");
		session.update();
		assertEquals("aaa", session.getSipPhoneNumber());
		session.setSipPhoneNumber("wlifeng");
		session.update();
		assertEquals("wlifeng", session.getSipPhoneNumber());
		session.logout();
	}

	private boolean checkSuccess(InputStream content)
			throws RecaptchaFailedException {
		Scanner scanner = new Scanner(content, "UTF-8");
		try {
			String result = scanner
					.findWithinHorizon(
							"(You will be notified via email if the process has been completed|Invalid verification codes)",
							2048);
			if (result == null) {
				return false;
			}
			if ("Invalid verification codes".equals(result)) {
				throw new RecaptchaFailedException();
			}
			if ("You will be notified via email if the process has been completed"
					.equalsIgnoreCase(result)) {
				return true;
			} else {
				return false;
			}
		} finally {
			scanner.close();
		}
	}
}
