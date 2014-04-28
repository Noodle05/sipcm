/**
 *
 */
package com.mycallstation.googlevoice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.Gson;
import com.mycallstation.common.AuthenticationException;
import com.mycallstation.common.BaseConfiguration;
import com.mycallstation.constant.McsConstant;
import com.mycallstation.googlevoice.message.CheckMessageResult;
import com.mycallstation.googlevoice.result.CallResult;
import com.mycallstation.googlevoice.result.CheckForwardingVerifiedResult;
import com.mycallstation.googlevoice.result.CheckIllegalSharingResult;
import com.mycallstation.googlevoice.result.EditPhoneResult;
import com.mycallstation.googlevoice.setting.GoogleVoiceConfig;
import com.mycallstation.googlevoice.setting.Phone;
import com.mycallstation.googlevoice.setting.Settings;
import com.mycallstation.googlevoice.util.Utility;
import com.mycallstation.util.HttpUtils;

/**
 * @author Wei Gao
 */
@Component("googleVoiceSession")
@Scope("prototype")
public class GoogleVoiceSession implements Serializable {
	private static final Logger logger = LoggerFactory
			.getLogger(GoogleVoiceSession.class);

	private static final long serialVersionUID = 727761632230756155L;

	private static final String LOGIN_URL = "https://www.google.com/accounts/ClientLogin";
	private static final String LOGOUT_URL = "https://www.google.com/accounts/Logout";
	private static final String GV_URL = "https://www.google.com/voice/";
	private static final String CALL_URL = "https://www.google.com/voice/call/connect/";
	private static final String CONCEL_URL = "https://www.google.com/voice/call/cancel/";

	private static final String PHONE_SETTING_URL = "https://www.google.com/voice/settings/tab/phones";
	private static final String EDIT_GENERAL_SETTINGS_URL = "https://www.google.com/voice/settings/editGeneralSettings/";
	private static final String DELETE_PHONE_URL = "https://www.google.com/voice/settings/deleteForwarding/";
	private static final String EDIT_ADD_PHONE_URL = "https://www.google.com/voice/settings/editForwarding/";
	private static final String SET_IN_VERIFICATION_URL = "https://www.google.com/voice/settings/setInVerification";
	// Request: phoneId = xxx
	// Response: "ok": true, "needReclaim":true, "reclaimCheckResult":1
	private static final String CHECK_ILLEGAL_SHARING_URL = "https://www.google.com/voice/settings/checkIllegalSharing";
	// Request: code: xx, forwardingNumber = +1xxxx, phoneId = xxx, phoneType =
	// x, subscriberNumber = undefined
	// Response: "ok":true,"verified":false/true,"diversionName":"","carrier":""
	private static final String VERIFY_FORWARDING_URL = "https://www.google.com/voice/call/verifyForwarding";
	// Get request: phoneId=xx
	// {"ok":true,"verified":false,"diversionNum":"","carrier":""}
	// {"ok":true,"verified":true,"diversionNum":"","carrier":""}
	private static final String CHECK_FORWARDING_VERIFIED_URL = "https://www.google.com/voice/settings/checkForwardingVerified";
	// Request: phoneId = xxx, enabled = 0/1
	// Response: "ok": true
	private static final String EDIT_DEFAULT_FORWARDING_URL = "https://www.google.com/voice/settings/editDefaultForwarding/";

	private static final String GV_SERVICE = "grandcentral";

	private static final Pattern authTokenPattern = Pattern
			.compile("^Auth=(.+)$");

	static final Pattern gcDataPattern = Pattern
			.compile(
					"\\s*var\\s+_gcData\\s*=\\s*(\\{.*\\});\\s*_gvRun\\(_gcData,\\s*'en_US',\\s*true\\);\\s*",
					Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNIX_LINES);
	static final Pattern errorPattern = Pattern.compile("^Error=(.*)$");
	static final Pattern captchaTokenPattern = Pattern
			.compile("^CaptchaToken=(.+)$");
	static final Pattern captchaUrlPattern = Pattern
			.compile("^CaptchaUrl=(.+)$");
	static final Pattern xpcRPattern = Pattern
			.compile("new _cd\\('(.*)', null, null, '(?:.*)'\\);");

	private String username;
	private String password;
	private String myNumber;
	HttpClient httpClient;
	private String authToken;
	private String rnrSe;
	private String version = "24230371";
	private String xpcUrl;
	private int maxRetry = 1;
	private volatile boolean cancelCall;
	private volatile boolean loggedIn;

	@Resource(name = "systemConfiguration")
	BaseConfiguration appConfig;

	@Resource(name = "httpConnectionManager")
	private ClientConnectionManager clientConntectionManager;

	@PostConstruct
	public void init() {
		HttpParams params = new BasicHttpParams();
		int connTimeout = appConfig.getHttpClientConnectionTimeout();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				connTimeout);
		DefaultHttpClient client = new DefaultHttpClient(
				clientConntectionManager, params);
		client.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				request.setHeader("User-agent", McsConstant.USER_AGENT);
			}
		});
		httpClient = client;
	}

	public void login() throws ClientProtocolException, IOException,
			AuthenticationException, HttpResponseException {
		if (!loggedIn) {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to login.");
			}
			HttpPost login = new HttpPost(LOGIN_URL);
			List<NameValuePair> ps = new ArrayList<NameValuePair>(5);
			ps.add(new BasicNameValuePair("Email", username));
			ps.add(new BasicNameValuePair("Passwd", password));
			ps.add(new BasicNameValuePair("service", GV_SERVICE));
			ps.add(new BasicNameValuePair("accountType", "HOSTED_OR_GOOGLE"));
			ps.add(new BasicNameValuePair("source", appConfig
					.getGoogleAuthenticationAppname()));
			HttpEntity oe = new UrlEncodedFormEntity(ps);
			login.setEntity(oe);
			HttpResponse response = httpClient.execute(login);
			try {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					String charset = HttpUtils.getCharset(entity);
					InputStreamReader isr = new InputStreamReader(
							entity.getContent(), charset);
					BufferedReader reader = new BufferedReader(isr);
					try {
						String tmp = null;
						while ((tmp = reader.readLine()) != null && !loggedIn) {
							Matcher m = authTokenPattern.matcher(tmp);
							if (m.matches()) {
								authToken = m.group(1);
								loggedIn = true;
							}
						}
					} finally {
						reader.close();
					}
				} else {
					parseError(response.getEntity());
				}
			} finally {
				login.abort();
			}
			if (!loggedIn) {
				throw new AuthenticationException("Not able to login");
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Logged in.");
			}
			HttpGet gvPage = new HttpGet(GV_URL);
			prepairAuthToken(gvPage);
			response = httpClient.execute(gvPage);
			HttpUtils.checkResponse(gvPage, response, logger);
			HttpEntity entity = response.getEntity();
			String charset = HttpUtils.getCharset(entity);
			InputStream is = entity.getContent();
			String str = null;
			try {
				Document dom = Jsoup.parse(is, charset,
						"https://www.google.com");
				Elements scripts = dom.select("script[type$=javascript]").not(
						"script[src]");
				if (scripts != null) {
					for (Element s : scripts) {
						String t = s.html();
						Matcher m = gcDataPattern.matcher(t);
						if (m.matches()) {
							str = m.group(1);
							break;
						}
					}
				}
			} finally {
				is.close();
			}
			String xurl = null;
			if (str != null) {
				// Remove xml comments inside java script.
				str = str.replaceAll("<!--[^>]+-->", "");
				str = str.replaceAll("(?s),\\s*}", "}");
				Gson gson = Utility.getGson();
				RnrSeData data = gson.fromJson(str, RnrSeData.class);
				rnrSe = data.get_rnr_se();
				version = data.getV();
				xurl = data.getXpcUrl();
			}
			if (rnrSe == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Cannot find rnr.");
				}
				throw new NoRnrSeException();
			}
			if (xurl != null) {
				getXpcUrl(xurl);
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Find rnr: {}", rnrSe);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Google voice session for \"{}\" logged in.",
						username);
			}
		}
	}

	private void getXpcUrl(String baseUrl) {
		xpcUrl = null;
		String url = baseUrl + "/voice/xpc";
		HttpGet request = new HttpGet(url);
		prepairAuthToken(request);
		try {
			HttpResponse response = httpClient.execute(request);
			HttpUtils.checkResponse(request, response, logger);
			HttpEntity entity = response.getEntity();
			String charset = HttpUtils.getCharset(entity);
			InputStream is = entity.getContent();
			String str = null;
			try {
				Document dom = Jsoup.parse(is, charset, baseUrl);
				Elements list = dom.select("body[onload]");
				if (list != null) {
					for (Element e : list) {
						String a = e.attr("onload");
						if (a != null) {
							str = a;
							break;
						}
					}
				}
			} finally {
				is.close();
			}
			if (str != null) {
				Matcher m = xpcRPattern.matcher(str);
				if (m.matches()) {
					String r = m.group(1);
					if (r != null) {
						url = baseUrl + "/voice/xpc/checkMessages";
						List<NameValuePair> params = new ArrayList<NameValuePair>(
								1);
						params.add(new BasicNameValuePair("r", r));
						xpcUrl = HttpUtils.prepareGetUrl(url, params);
					} else {
						throw new Exception("Cannot find xpc r information.");
					}
				} else {
					throw new Exception("xpc r pattern doesn't match.");
				}
			} else {
				throw new Exception("Cannot get xpc r information.");
			}
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn(
						"Error happened when getting xpc url info, voice mail notification may not work.",
						e);
			}
		}
	}

	private void prepairAuthToken(HttpMessage message) {
		message.setHeader("Authorization", "GoogleLogin auth=" + authToken);
	}

	public void logout() {
		if (loggedIn) {
			HttpGet callGet = new HttpGet(LOGOUT_URL);
			try {
				httpClient.execute(callGet);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Error happened when try to logout, will ignore it.",
							e);
				}
			} finally {
				try {
					callGet.abort();
				} catch (Throwable e) {
					// Ignore it.
				}
				authToken = null;
				rnrSe = null;
				loggedIn = false;
				cancelCall = false;
			}
		}
	}

	public boolean call(String destination, String phoneType)
			throws ClientProtocolException, IOException, HttpResponseException,
			AuthenticationException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (!cancelCall) {
			if (!loggedIn) {
				login();
			}
			HttpPost callPost = new HttpPost(CALL_URL);
			List<NameValuePair> ps = new ArrayList<NameValuePair>(6);
			ps.add(new BasicNameValuePair("outgoingNumber", destination));
			ps.add(new BasicNameValuePair("forwardingNumber", myNumber));
			ps.add(new BasicNameValuePair("subscriberNumber", "undefined"));
			ps.add(new BasicNameValuePair("phoneType", phoneType));
			ps.add(new BasicNameValuePair("remember", "0"));
			ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
			HttpEntity en = new UrlEncodedFormEntity(ps);
			callPost.setEntity(en);
			prepairAuthToken(callPost);
			if (logger.isTraceEnabled()) {
				logger.trace(
						"Calling \"call\" method with target number {}, callback number {}, phone type {}",
						new Object[] { destination, myNumber, phoneType });
			}
			CallResult result = callMethod(callPost, 0, CallResult.class);
			return result.isSuccess();
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("This call already cancelled.");
			}
			return false;
		}
	}

	public boolean cancel() throws ClientProtocolException, IOException,
			HttpResponseException, AuthenticationException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (logger.isDebugEnabled()) {
			logger.debug("Cancelling call.");
		}
		cancelCall = true;
		if (!loggedIn) {
			login();
		}
		HttpPost callPost = new HttpPost(CONCEL_URL);
		List<NameValuePair> ps = new ArrayList<NameValuePair>(4);
		ps.add(new BasicNameValuePair("outgoingNumber", "undefined"));
		ps.add(new BasicNameValuePair("forwardingNumber", "undefined"));
		ps.add(new BasicNameValuePair("cancelType", "C2C"));
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		prepairAuthToken(callPost);
		if (logger.isTraceEnabled()) {
			logger.trace("Call \"cancel\" method.");
		}
		CallResult result = callMethod(callPost, 0, CallResult.class);
		return result.isSuccess();
	}

	public int checkNewMessage() throws ClientProtocolException,
			SecurityException, IllegalArgumentException, IOException,
			HttpResponseException, AuthenticationException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		if (logger.isDebugEnabled()) {
			logger.debug("Checking new messages.");
		}
		if (!loggedIn) {
			login();
		}
		if (xpcUrl != null) {
			HttpGet request = new HttpGet(xpcUrl);
			prepairAuthToken(request);
			if (logger.isTraceEnabled()) {
				logger.trace("Call \"check message\" method.");
			}
			CheckMessageResult result = callMethod(request, 0,
					CheckMessageResult.class);
			if (result != null && result.getMessageInfo() != null) {
				return result.getMessageInfo().getVoicemail();
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public GoogleVoiceConfig getGoogleVoiceSetting() throws IOException,
			ParserConfigurationException, SAXException, IllegalStateException,
			HttpResponseException, AuthenticationException {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting google voice settings");
		}
		if (!loggedIn) {
			login();
		}
		List<NameValuePair> qparams = new ArrayList<NameValuePair>(1);
		qparams.add(new BasicNameValuePair("v", version));
		HttpGet request = new HttpGet(HttpUtils.prepareGetUrl(
				PHONE_SETTING_URL, qparams));
		prepairAuthToken(request);
		if (logger.isTraceEnabled()) {
			logger.trace("Sending request");
		}
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
			if (logger.isDebugEnabled()) {
				logger.debug("Call failed, parsing failed reason.");
			}
			parseError(response.getEntity());
		}
		HttpUtils.checkResponse(request, response, logger);
		HttpEntity entity = response.getEntity();
		String charset = HttpUtils.getCharset(entity);
		InputStreamReader reader = new InputStreamReader(entity.getContent(),
				charset);
		InputSource is = new InputSource(reader);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			SAXHandler handler = new SAXHandler();
			if (logger.isTraceEnabled()) {
				logger.trace("Parsing response xml.");
			}
			parser.parse(is, handler);
			String jsonStr = handler.getJson();
			if (jsonStr != null) {
				if (logger.isTraceEnabled()) {
					logger.trace("Found json string. {}", jsonStr);
				}
				Gson gson = Utility.getGson();
				GoogleVoiceConfig config = gson.fromJson(jsonStr,
						GoogleVoiceConfig.class);
				return config;
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Parse success, but didn't found json string.");
				}
			}
		} finally {
			try {
				reader.close();
			} catch (Throwable e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Error happened when close reader, ignore it.",
							e);
				}
			}
		}
		return null;
	}

	public CallResult editGeneralSettings(Settings settings)
			throws ClientProtocolException, SecurityException,
			IllegalArgumentException, IOException, HttpResponseException,
			AuthenticationException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (settings == null) {
			throw new NullPointerException();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Editing general settings", settings);
		}
		if (!loggedIn) {
			login();
		}
		HttpPost callPost = new HttpPost(EDIT_GENERAL_SETTINGS_URL);
		List<NameValuePair> ps = new ArrayList<NameValuePair>(15);

		ps.add(new BasicNameValuePair("directConnect", settings
				.isDirectConnect() ? "1" : "0"));
		ps.add(new BasicNameValuePair("directRtp", Integer.toString(settings
				.getDirectRtp())));
		ps.add(new BasicNameValuePair("doNotDisturb",
				settings.isDoNotDisturb() ? "1" : "0"));
		if (settings.isDoNotDisturb()) {
			ps.add(new BasicNameValuePair("doNotDisturbExpiration", Long
					.toString(settings.getDoNotDisturbExpiration())));
		} else {
			ps.add(new BasicNameValuePair("doNotDisturbExpiration", "-1"));
		}
		ps.add(new BasicNameValuePair("emailNotificationActive", settings
				.isEmailNotificationActive() ? "1" : "0"));
		ps.add(new BasicNameValuePair("emailNotificationAddress", settings
				.getEmailNotificationAddress()));
		ps.add(new BasicNameValuePair("filterGlobalSpam", Integer
				.toString(settings.getFilterGlobalSpam())));
		ps.add(new BasicNameValuePair("missedToEmail", settings
				.isMissedToEmail() ? "1" : "0"));
		ps.add(new BasicNameValuePair("missedToInbox", settings
				.isMissedToInbox() ? "1" : "0"));
		if (!settings.isDirectConnect()) {
			ps.add(new BasicNameValuePair("screenBehavior", Integer
					.toString(settings.getScreenBehavior())));
		} else {
			ps.add(new BasicNameValuePair("screenBehavior", "0"));
		}
		ps.add(new BasicNameValuePair("smsToEmailActive", settings
				.isSmsToEmailActive() ? "1" : "0"));
		ps.add(new BasicNameValuePair("smsToEmailSubject", settings
				.isSmsToEmailSubject() ? "1" : "0"));
		ps.add(new BasicNameValuePair("useDidAsCallerId", settings
				.isUseDidAsCallerId() ? "1" : "0"));
		ps.add(new BasicNameValuePair("useDidAsSmsSource", settings
				.isUseDidAsSource() ? "1" : "0"));

		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		prepairAuthToken(callPost);
		if (logger.isTraceEnabled()) {
			logger.trace("Calling general settings.");
		}
		CallResult result = callMethod(callPost, 0, CallResult.class);
		return result;
	}

	public EditPhoneResult editPhone(Phone phone)
			throws ClientProtocolException, IOException, HttpResponseException,
			AuthenticationException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		EditPhoneResult result = editPhone(phone, true);
		if (result.isSuccess()) {
			result = editPhone(phone, false);
		}
		return result;
	}

	private EditPhoneResult editPhone(Phone phone, boolean dryRun)
			throws ClientProtocolException, IOException, HttpResponseException,
			AuthenticationException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (phone == null) {
			throw new NullPointerException("Cannot delete non-exists phone.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding phone: {}", phone);
		}
		if (!loggedIn) {
			login();
		}
		HttpPost callPost = new HttpPost(EDIT_ADD_PHONE_URL);
		List<NameValuePair> ps = new ArrayList<NameValuePair>(dryRun ? 15 : 14);
		if (dryRun) {
			ps.add(new BasicNameValuePair("dryRun", "1"));
		}
		ps.add(new BasicNameValuePair("id", Integer.toString(phone.getId())));
		ps.add(new BasicNameValuePair("type", Integer.toString(phone.getType()
				.getValue())));
		ps.add(new BasicNameValuePair("name", phone.getName()));
		ps.add(new BasicNameValuePair("phoneNumber", phone.getPhoneNumber()));
		ps.add(new BasicNameValuePair("policyBitmask", Integer.toString(phone
				.getPolicyBitmask().getValue())));
		ps.add(new BasicNameValuePair("redirectToVoicemail", phone
				.isRedirectToVoicemail() ? "1" : "0"));
		ps.add(new BasicNameValuePair("smsEnabled", phone.isSmsEnabled() ? "1"
				: "0"));
		switch (phone.getScheduleSet()) {
		case FALSE:
			ps.add(new BasicNameValuePair("ringwd", "0"));
			ps.add(new BasicNameValuePair("fromTimewd0", "9:00am"));
			ps.add(new BasicNameValuePair("toTimewd0", "5:00pm"));
			ps.add(new BasicNameValuePair("ringwe", "0"));
			ps.add(new BasicNameValuePair("fromTimewe0", "9:00am"));
			ps.add(new BasicNameValuePair("toTimewe0", "5:00pm"));
			break;
		case TRUE:
			if (phone.getWd() != null && phone.getWd().isAllDay()) {
				ps.add(new BasicNameValuePair("ringwd", "1"));
			} else {
				ps.add(new BasicNameValuePair("ringwd", "0"));
			}
			ps.add(new BasicNameValuePair("fromTimewd0", "9:00am"));
			ps.add(new BasicNameValuePair("toTimewd0", "5:00pm"));
			if (phone.getWe() != null && phone.getWe().isAllDay()) {
				ps.add(new BasicNameValuePair("ringwe", "1"));
			} else {
				ps.add(new BasicNameValuePair("ringwe", "0"));
			}
			ps.add(new BasicNameValuePair("fromTimewe0", "9:00am"));
			ps.add(new BasicNameValuePair("toTimewe0", "5:00pm"));
			break;
		case ONE:
			if (phone.getWd() != null) {
				if (phone.getWd().isAllDay()) {
					ps.add(new BasicNameValuePair("ringwd", "1"));
					ps.add(new BasicNameValuePair("fromTimewd0", "9:00am"));
					ps.add(new BasicNameValuePair("toTimewd0", "5:00pm"));
				} else {
					if (phone.getWd().getTimes() != null
							&& phone.getWd().getTimes().length > 0) {
						ps.add(new BasicNameValuePair("ringwd", "2"));
						for (int i = 0; i < phone.getWd().getTimes().length; i++) {
							ps.add(new BasicNameValuePair("fromTimewd" + i,
									phone.getWd().getTimes()[i].getStartTime()));
							ps.add(new BasicNameValuePair("toTimewd0" + i,
									phone.getWd().getTimes()[i].getEndTime()));
						}
					} else {
						ps.add(new BasicNameValuePair("ringwd", "0"));
						ps.add(new BasicNameValuePair("fromTimewd0", "9:00am"));
						ps.add(new BasicNameValuePair("toTimewd0", "5:00pm"));
					}
				}
			} else {
				ps.add(new BasicNameValuePair("ringwd", "0"));
				ps.add(new BasicNameValuePair("fromTimewd0", "9:00am"));
				ps.add(new BasicNameValuePair("toTimewd0", "5:00pm"));
			}
			if (phone.getWe() != null) {
				if (phone.getWe().isAllDay()) {
					ps.add(new BasicNameValuePair("ringwe", "1"));
					ps.add(new BasicNameValuePair("fromTimewe0", "9:00am"));
					ps.add(new BasicNameValuePair("toTimewe0", "5:00pm"));
				} else {
					if (phone.getWe().getTimes() != null
							&& phone.getWe().getTimes().length > 0) {
						ps.add(new BasicNameValuePair("ringwe", "2"));
						for (int i = 0; i < phone.getWe().getTimes().length; i++) {
							ps.add(new BasicNameValuePair("fromTimewe" + i,
									phone.getWe().getTimes()[i].getStartTime()));
							ps.add(new BasicNameValuePair("toTimewe0" + i,
									phone.getWe().getTimes()[i].getEndTime()));
						}
					} else {
						ps.add(new BasicNameValuePair("ringwe", "0"));
						ps.add(new BasicNameValuePair("fromTimewe0", "9:00am"));
						ps.add(new BasicNameValuePair("toTimewe0", "5:00pm"));
					}
				}
			} else {
				ps.add(new BasicNameValuePair("ringwe", "0"));
				ps.add(new BasicNameValuePair("fromTimewe0", "9:00am"));
				ps.add(new BasicNameValuePair("toTimewe0", "5:00pm"));
			}
			break;
		}
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		prepairAuthToken(callPost);
		if (logger.isTraceEnabled()) {
			logger.trace("Calling edit/add phone number.");
		}
		EditPhoneResult result = callMethod(callPost, 0, EditPhoneResult.class);
		return result;
	}

	public boolean deletePhone(Phone phone) throws ClientProtocolException,
			IOException, HttpResponseException, AuthenticationException,
			SecurityException, IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (phone == null) {
			throw new NullPointerException("Cannot delete non-exists phone.");
		}
		if (phone.getId() <= 0) {
			throw new IllegalArgumentException(
					"Phone id less equal to 0, cannot delete.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting phone: {}", phone);
		}
		if (!loggedIn) {
			login();
		}
		HttpPost callPost = new HttpPost(DELETE_PHONE_URL);
		List<NameValuePair> ps = new ArrayList<NameValuePair>(2);
		ps.add(new BasicNameValuePair("id", Integer.toString(phone.getId())));
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		prepairAuthToken(callPost);
		if (logger.isTraceEnabled()) {
			logger.trace("Call \"delete phone\" method.");
		}
		CallResult result = callMethod(callPost, 0, CallResult.class);
		return result.isSuccess();
	}

	public CheckIllegalSharingResult checkIllegalSharing(Phone phone)
			throws ClientProtocolException, SecurityException,
			IllegalArgumentException, IOException, HttpResponseException,
			AuthenticationException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (phone == null) {
			throw new NullPointerException("Cannot check for non-exists phone.");
		}
		if (phone.getId() <= 0) {
			throw new IllegalArgumentException(
					"Phone id less equal to 0, cannot check.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Checking illegal sharing for phone: {}", phone);
		}
		if (!loggedIn) {
			login();
		}
		HttpPost callPost = new HttpPost(CHECK_ILLEGAL_SHARING_URL);
		List<NameValuePair> ps = new ArrayList<NameValuePair>(2);
		ps.add(new BasicNameValuePair("phoneId",
				Integer.toString(phone.getId())));
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		prepairAuthToken(callPost);
		if (logger.isTraceEnabled()) {
			logger.trace("Call \"delete phone\" method.");
		}
		CheckIllegalSharingResult result = callMethod(callPost, 0,
				CheckIllegalSharingResult.class);
		return result;
	}

	public boolean editDefaultForwarding(Phone phone, boolean enable)
			throws ClientProtocolException, SecurityException,
			IllegalArgumentException, IOException, HttpResponseException,
			AuthenticationException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (phone == null) {
			throw new NullPointerException();
		}
		if (phone.getId() <= 0) {
			throw new IllegalArgumentException(
					"Phone id less equal to 0, cannot edit.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Edit default forwarding for phone: {} to set enable to {}",
					phone, enable);
		}
		if (!loggedIn) {
			login();
		}
		HttpPost callPost = new HttpPost(EDIT_DEFAULT_FORWARDING_URL);
		List<NameValuePair> ps = new ArrayList<NameValuePair>(3);
		ps.add(new BasicNameValuePair("phoneId",
				Integer.toString(phone.getId())));
		ps.add(new BasicNameValuePair("enabled", enable ? "1" : "0"));
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		prepairAuthToken(callPost);
		if (logger.isTraceEnabled()) {
			logger.trace("Call \"delete phone\" method.");
		}
		CallResult result = callMethod(callPost, 0, CallResult.class);
		return result.isSuccess();
	}

	public CheckForwardingVerifiedResult checkForwardingVerified(Phone phone)
			throws ClientProtocolException, SecurityException,
			IllegalArgumentException, IOException, HttpResponseException,
			AuthenticationException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (phone == null) {
			throw new NullPointerException();
		}
		if (phone.getId() <= 0) {
			throw new IllegalArgumentException(
					"Phone id less equal to 0, cannot check.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Checking forwarding verified for phone: {}", phone);
		}
		if (!loggedIn) {
			login();
		}
		List<NameValuePair> qparams = new ArrayList<NameValuePair>(1);
		qparams.add(new BasicNameValuePair("phoneId", Integer.toString(phone
				.getId())));
		HttpGet request = new HttpGet(HttpUtils.prepareGetUrl(
				CHECK_FORWARDING_VERIFIED_URL, qparams));
		prepairAuthToken(request);

		if (logger.isTraceEnabled()) {
			logger.trace("Call \"checkForwardingVerified\" method.");
		}
		CheckForwardingVerifiedResult result = callMethod(request, 0,
				CheckForwardingVerifiedResult.class);
		return result;
	}

	public boolean setInVerification(Phone phone, boolean inVerification)
			throws ClientProtocolException, IOException, HttpResponseException,
			AuthenticationException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (phone == null) {
			throw new NullPointerException(
					"Cannot set inVerification non-exists phone.");
		}
		if (phone.getId() <= 0) {
			throw new IllegalArgumentException(
					"Phone id less equal to 0, cannot set inVerification.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Setting inVerification for phone: {}", phone);
		}
		if (!loggedIn) {
			login();
		}
		HttpPost callPost = new HttpPost(SET_IN_VERIFICATION_URL);
		List<NameValuePair> ps = new ArrayList<NameValuePair>(3);
		ps.add(new BasicNameValuePair("phoneId",
				Integer.toString(phone.getId())));
		if (inVerification) {
			ps.add(new BasicNameValuePair("isInVerification", "1"));
		} else {
			ps.add(new BasicNameValuePair("isInVerification", "0"));
		}
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		prepairAuthToken(callPost);
		if (logger.isTraceEnabled()) {
			logger.trace("Call \"setInVerification for phone\" method.");
		}
		CallResult result = callMethod(callPost, 0, CallResult.class);
		return result.isSuccess();
	}

	public boolean verifyForwarding(Phone phone, int code)
			throws ClientProtocolException, SecurityException,
			IllegalArgumentException, IOException, HttpResponseException,
			AuthenticationException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (phone == null) {
			throw new NullPointerException(
					"Cannot set inVerification non-exists phone.");
		}
		if (phone.getId() <= 0) {
			throw new IllegalArgumentException(
					"Phone id less equal to 0, cannot set inVerification.");
		}
		if (code <= 0 || code > 100) {
			throw new IllegalArgumentException(
					"verify code need to between 1 to 99");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Setting inVerification for phone: {}", phone);
		}
		if (!loggedIn) {
			login();
		}
		HttpPost callPost = new HttpPost(VERIFY_FORWARDING_URL);
		List<NameValuePair> ps = new ArrayList<NameValuePair>(6);
		ps.add(new BasicNameValuePair("phoneId",
				Integer.toString(phone.getId())));
		ps.add(new BasicNameValuePair("forwardingNumber", phone
				.getPhoneNumber()));
		ps.add(new BasicNameValuePair("code", code < 10 ? "0"
				+ Integer.toString(code) : Integer.toString(code)));
		ps.add(new BasicNameValuePair("phoneType", Integer.toString(phone
				.getType().getValue())));
		ps.add(new BasicNameValuePair("subscriberNumber", "undefined"));

		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		prepairAuthToken(callPost);
		if (logger.isTraceEnabled()) {
			logger.trace("Call \"setInVerification for phone\" method.");
		}
		CallResult result = callMethod(callPost, 0, CallResult.class);
		return result.isSuccess();
	}

	private <T extends CallResult> T callMethod(HttpUriRequest request,
			int retry, Class<T> clazz) throws ClientProtocolException,
			IOException, HttpResponseException, AuthenticationException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		T result = null;
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
			if (logger.isTraceEnabled()) {
				logger.trace("Request rejected.");
			}
			if (retry < maxRetry) {
				request.abort();
				if (logger.isTraceEnabled()) {
					logger.trace("Retry with login first.");
				}
				login();
				return callMethod(request, retry++, clazz);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Call failed, parsing failed reason.");
				}
				parseError(response.getEntity());
			}
		}
		HttpUtils.checkResponse(request, response, logger);
		String body = EntityUtils.toString(response.getEntity());
		if (logger.isTraceEnabled()) {
			logger.trace("Method return body: {}", body);
		}
		Constructor<T> c = clazz.getConstructor(String.class);
		result = c.newInstance(body);
		if (logger.isDebugEnabled()) {
			logger.debug("Call result: {}", result);
		}
		return result;
	}

	private void parseError(HttpEntity entity)
			throws GoogleAuthenticationException, IllegalStateException,
			IOException, HttpResponseException {
		if (entity != null) {
			String charset = HttpUtils.getCharset(entity);
			InputStreamReader isr = new InputStreamReader(entity.getContent(),
					charset);
			BufferedReader reader = new BufferedReader(isr);
			try {
				String tmp;
				AuthenticationErrorCode errorCode = null;
				String captchaToken = null;
				String captchaUrl = null;
				while ((tmp = reader.readLine()) != null) {
					Matcher m = errorPattern.matcher(tmp);
					if (m.matches()) {
						errorCode = AuthenticationErrorCode.valueOf(m.group(1));
					} else {
						m = captchaTokenPattern.matcher(tmp);
						if (m.matches()) {
							captchaToken = m.group(1);
						} else {
							m = captchaUrlPattern.matcher(tmp);
							if (m.matches()) {
								captchaUrl = m.group(1);
							}
						}
					}
				}
				if (errorCode != null) {
					GoogleAuthenticationException.throwProperException(
							errorCode, captchaToken, captchaUrl);
				}
			} finally {
				reader.close();
			}
		}
		throw new HttpResponseException(HttpStatus.SC_FORBIDDEN,
				"Error happened when parse error page. Cannot find error code.");
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		if (!password.equals(this.password)) {
			logout();
			this.password = password;
		}
	}

	public void setMyNumber(String myNumber) {
		this.myNumber = myNumber;
	}

	public String getMyNumber() {
		return myNumber;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hc = 71;
		if (username != null) {
			hc += username.toUpperCase().hashCode() * 11;
		}
		return hc;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof GoogleVoiceSession)) {
			return false;
		}
		if (username == null) {
			return false;
		}
		final GoogleVoiceSession other = (GoogleVoiceSession) obj;
		if (other.username == null) {
			return false;
		}
		return username.equalsIgnoreCase(other.username);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GoogleVoice[");
		if (username != null) {
			sb.append("username=").append(username);
		} else {
			sb.append("unknown");
		}
		sb.append("]");
		return sb.toString();
	}
}

class RnrSeData implements Serializable {
	private static final long serialVersionUID = 3873653909704915557L;

	private String v;
	private String _rnr_se;
	private String xpcUrl;

	/**
	 * @return the v
	 */
	String getV() {
		return v;
	}

	/**
	 * @return the _rnr_se
	 */
	String get_rnr_se() {
		return _rnr_se;
	}

	/**
	 * @return the xpcUrl
	 */
	String getXpcUrl() {
		return xpcUrl;
	}
}

class SAXHandler extends DefaultHandler {
	private char[] data;
	private int cursor;

	private boolean inJson;
	private boolean jsonSetted;

	public SAXHandler() {
		data = new char[1024];
		cursor = 0;
		inJson = false;
		jsonSetted = false;
	}

	public String getJson() {
		return jsonSetted ? String.valueOf(data, 0, cursor) : null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (!inJson && !jsonSetted && qName.equalsIgnoreCase("json")) {
			inJson = true;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (inJson && qName.equalsIgnoreCase("json")) {
			inJson = false;
			jsonSetted = true;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (inJson) {
			if ((data.length - cursor) < length) {
				char[] tmp = new char[cursor + length];
				System.arraycopy(data, 0, tmp, 0, cursor);
				data = tmp;
			}
			System.arraycopy(ch, start, data, cursor, length);
			cursor += length;
		}
	}
}
