/**
 * 
 */
package com.mycallstation.googlevoice;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

import com.mycallstation.common.AuthenticationException;
import com.mycallstation.common.TestConfiguration;
import com.mycallstation.constant.McsConstant;
import com.mycallstation.googlevoice.result.CallResult;
import com.mycallstation.googlevoice.result.CheckForwardingVerifiedResult;
import com.mycallstation.googlevoice.result.CheckIllegalSharingResult;
import com.mycallstation.googlevoice.result.EditPhoneResult;
import com.mycallstation.googlevoice.setting.GoogleVoiceConfig;
import com.mycallstation.googlevoice.setting.Phone;
import com.mycallstation.googlevoice.setting.PhoneType;
import com.mycallstation.googlevoice.setting.ScheduleSet;
import com.mycallstation.googlevoice.setting.Settings;
import com.mycallstation.googlevoice.setting.VoiceMailAccessPolicy;

/**
 * @author wgao
 * 
 */
public class GoogleVoiceFunctionTest {
	private static final String username = "wlifeng@yahoo.com";
	private static final String password = "wwww4321";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Ignore
	@Test
	public void test() throws ClientProtocolException, IOException,
			AuthenticationException, HttpResponseException,
			IllegalStateException, ParserConfigurationException, SAXException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.login();
		try {
			GoogleVoiceConfig config = gvSession.getGoogleVoiceSetting();
			assertNotNull(config);
		} finally {
			gvSession.logout();
		}
	}

	@Test
	public void testSettings() throws ClientProtocolException, IOException,
			AuthenticationException, HttpResponseException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IllegalStateException,
			ParserConfigurationException, SAXException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.login();
		try {
			GoogleVoiceConfig config = gvSession.getGoogleVoiceSetting();
			assertNotNull(config);
			Settings settings = config.getSettings();
			assertNotNull(settings);

			settings.setDirectConnect(false);
			settings.setUseDidAsCallerId(true);
			settings.setUseDidAsSource(true);
			CallResult result = gvSession.editGeneralSettings(settings);
			assertTrue(result.isSuccess());
			config = gvSession.getGoogleVoiceSetting();
			settings = config.getSettings();
			assertFalse(settings.isDirectConnect());
			assertTrue(settings.isUseDidAsCallerId());
			assertTrue(settings.isUseDidAsSource());

			settings.setDirectConnect(true);
			settings.setUseDidAsCallerId(false);
			settings.setUseDidAsSource(false);
			result = gvSession.editGeneralSettings(settings);
			assertTrue(result.isSuccess());
			config = gvSession.getGoogleVoiceSetting();
			settings = config.getSettings();
			assertTrue(settings.isDirectConnect());
			assertFalse(settings.isUseDidAsCallerId());
			assertFalse(settings.isUseDidAsSource());

		} finally {
			gvSession.logout();
		}
	}

	@Test
	public void testAddDeletePhone() throws ClientProtocolException,
			IOException, AuthenticationException, HttpResponseException,
			SecurityException, IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IllegalStateException,
			ParserConfigurationException, SAXException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.login();
		String phoneNumber = "+12063095310";
		try {
			GoogleVoiceConfig config = gvSession.getGoogleVoiceSetting();
			Phone phone = getPhoneByNumber(config, phoneNumber);
			if (phone != null) {
				boolean result = gvSession.deletePhone(phone);
				assertTrue(result);
				config = gvSession.getGoogleVoiceSetting();
				phone = getPhoneByNumber(config, phoneNumber);
				assertNull(phone);
			}
			phone = new Phone();
			phone.setPhoneNumber("+12063095310");
			phone.setScheduleSet(ScheduleSet.FALSE);
			phone.setName("IPKall");
			phone.setType(PhoneType.HOME);
			phone.setPolicyBitmask(VoiceMailAccessPolicy.YES_WITHOUT_PIN);
			EditPhoneResult result = gvSession.editPhone(phone);
			assertTrue(result.isSuccess());
			config = gvSession.getGoogleVoiceSetting();
			phone = getPhoneByNumber(config, phoneNumber);
			assertNotNull(phone);
		} finally {
			gvSession.logout();
		}
	}

	@Ignore
	@Test
	public void checkIllegalSharingTest() throws ClientProtocolException,
			IOException, AuthenticationException, HttpResponseException,
			SecurityException, IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IllegalStateException,
			ParserConfigurationException, SAXException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.login();
		try {
			GoogleVoiceConfig config = gvSession.getGoogleVoiceSetting();
			assertNotNull(config);
			Phone phone = null;
			for (Phone p : config.getPhones().values()) {
				if ("+12063095310".equalsIgnoreCase(p.getPhoneNumber())) {
					phone = p;
					break;
				}
			}
			assertNotNull(phone);
			CheckIllegalSharingResult result = gvSession
					.checkIllegalSharing(phone);
			assertTrue(result.isSuccess());
		} finally {
			gvSession.logout();
		}
	}

	@Ignore
	@Test
	public void editDefaultForwardingTest() throws ClientProtocolException,
			IOException, AuthenticationException, HttpResponseException,
			SecurityException, IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IllegalStateException,
			ParserConfigurationException, SAXException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.login();
		try {
			GoogleVoiceConfig config = gvSession.getGoogleVoiceSetting();
			assertNotNull(config);
			Phone phone = null;
			for (Phone p : config.getPhones().values()) {
				if ("+14089400907".equalsIgnoreCase(p.getPhoneNumber())) {
					phone = p;
					break;
				}
			}
			assertNotNull(phone);
			boolean result = gvSession.editDefaultForwarding(phone, false);
			assertTrue(result);
			config = gvSession.getGoogleVoiceSetting();
			assertNotNull(config.getSettings().getDisabledIdMap()
					.get(phone.getId()));
			assertTrue(config.getSettings().getDisabledIdMap()
					.get(phone.getId()));

			result = gvSession.editDefaultForwarding(phone, true);
			assertTrue(result);

			config = gvSession.getGoogleVoiceSetting();
			assertTrue(!config.getSettings().getDisabledIdMap()
					.containsKey(phone.getId())
					|| !config.getSettings().getDisabledIdMap()
							.get(phone.getId()));
		} finally {
			gvSession.logout();
		}
	}

	@Ignore
	@Test
	public void testCheckForwardingVerified() throws ClientProtocolException,
			IOException, AuthenticationException, HttpResponseException,
			SecurityException, IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IllegalStateException,
			ParserConfigurationException, SAXException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.login();
		try {
			GoogleVoiceConfig config = gvSession.getGoogleVoiceSetting();
			assertNotNull(config);
			Phone phone1 = null;
			Phone phone2 = null;
			for (Phone p : config.getPhones().values()) {
				if ("+14089400907".equalsIgnoreCase(p.getPhoneNumber())) {
					phone1 = p;
				} else if ("+12063095310".equalsIgnoreCase(p.getPhoneNumber())) {
					phone2 = p;
				}
			}
			assertNotNull(phone1);
			assertNotNull(phone2);
			CheckForwardingVerifiedResult result = gvSession
					.checkForwardingVerified(phone1);
			assertTrue(result.isSuccess());
			assertTrue(result.isVerified());

			boolean s = gvSession.setInVerification(phone2, true);
			assertTrue(s);

			result = gvSession.checkForwardingVerified(phone2);
			assertTrue(result.isSuccess());
			assertFalse(result.isVerified());
		} finally {
			gvSession.logout();
		}
	}

	@Ignore
	@Test
	public void testDeletePhone() throws ClientProtocolException, IOException,
			AuthenticationException, HttpResponseException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, IllegalStateException,
			ParserConfigurationException, SAXException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.login();
		try {
			GoogleVoiceConfig config = gvSession.getGoogleVoiceSetting();
			assertFalse(config.getPhones().isEmpty());
			Phone phone = config.getPhones().values().iterator().next();
			boolean result = gvSession.deletePhone(phone);
			assertTrue(result);
		} finally {
			gvSession.logout();
		}
	}

	@Test(expected = GoogleAuthenticationException.class)
	public void testLoginFailed() throws ClientProtocolException, IOException,
			AuthenticationException, HttpResponseException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.setPassword("aaa");
		gvSession.login();
		fail();
	}

	@Test
	public void testCheckMessage() throws ClientProtocolException, IOException,
			AuthenticationException, HttpResponseException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		GoogleVoiceSession gvSession = getGoogleVoiceSession();
		gvSession.login();
		int voiceMessage = gvSession.checkNewMessage();
		assertEquals(1, voiceMessage);
	}

	private Phone getPhoneByNumber(GoogleVoiceConfig config, String phoneNumber) {
		Phone phone = null;
		for (Phone p : config.getPhones().values()) {
			if (phoneNumber.equalsIgnoreCase(p.getPhoneNumber())) {
				phone = p;
				break;
			}
		}
		return phone;
	}

	private GoogleVoiceSession getGoogleVoiceSession() {
		GoogleVoiceSession gvSession = new TestGoogleVoiceSession();
		gvSession.appConfig = new TestConfiguration();
		gvSession.init();
		gvSession.setUsername(username);
		gvSession.setPassword(password);
		return gvSession;
	}

	private static class TestGoogleVoiceSession extends GoogleVoiceSession {
		private static final long serialVersionUID = -2702106674979025107L;

		public void init() {
			HttpParams params = new BasicHttpParams();
			int connTimeout = appConfig.getHttpClientConnectionTimeout();
			params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					connTimeout);
			DefaultHttpClient client = new DefaultHttpClient(params);
			client.addRequestInterceptor(new HttpRequestInterceptor() {
				@Override
				public void process(HttpRequest request, HttpContext context)
						throws HttpException, IOException {
					request.setHeader("User-agent", McsConstant.USER_AGENT);
				}
			});
			httpClient = client;
		}
	}
}
