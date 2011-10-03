/**
 * 
 */
package com.mycallstation.external.ipkall;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
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

import com.mycallstation.common.BaseConfiguration;
import com.mycallstation.constant.McsConstant;
import com.mycallstation.external.recaptcha.GoogleRecaptcha;
import com.mycallstation.external.recaptcha.GoogleRecaptchaManager;
import com.mycallstation.external.recaptcha.NoResponseException;
import com.mycallstation.external.recaptcha.RecaptchaFailedException;
import com.mycallstation.googlevoice.HttpResponseException;
import com.mycallstation.util.HttpUtils;

/**
 * @author wgao
 * 
 */
@Component("ipKallSession")
@Scope("prototype")
public class IPKallSession implements Serializable {
	private static final long serialVersionUID = 3164163873270041442L;

	private static final Logger logger = LoggerFactory
			.getLogger(IPKallSession.class);

	static final List<String> availableAreaCode = new ArrayList<String>(4);
	static final String[] _areaCode_ = new String[] { "206", "253", "360",
			"425" };
	static final Random random = new Random();

	static final String IPKALL_URL = "http://phone.ipkall.com";

	static final String RECAPTCHA_KEY = "6LfigQEAAAAAAGotL5h_MF0CAySSkSnLb_H_bFaP";
	// submit1: Submit, txtDID: phone number, txtPswd: password
	static final String LOGIN_URL = "http://phone.ipkall.com/process.asp?action=verify";
	static final String LOGOUT_URL = "http://phone.ipkall.com/logout.asp";
	static final String INFORMATION_URL = "http://phone.ipkall.com/update.asp";
	// cboAreaCode: area code, optType: SIP, submit1: Submit, txtEmail: email,
	// txtPhone: phone number, txtProxy: domain, txtPswd: password, txtSecs: 120
	// modify:
	// optType: SIP, submit1: Submit, txtEmail, txtPhone, txtProxy, txtPswd,
	// txtSecs
	static final String REGISTER_URL = "http://phone.ipkall.com/process.asp?action=addedit";
	// Get
	static final String CANCEL_URL = "http://phone.ipkall.com/process.asp?action=cancel";
	// submit1: Submit, txtEmail: email
	static final String FORGOT_URL = "http://phone.ipkall.com/process.asp?action=forgot";

	static final String REGISTER_SUCCESS_STRING = "You will be notified via email if the process has been completed";
	static final String VERIFICATION_FAILED_STRING = "Invalid verification codes";

	private GoogleRecaptcha recaptcha;
	private HttpClient httpClient;

	@Resource(name = "httpConnectionManager")
	ClientConnectionManager clientConntectionManager;

	@Resource(name = "systemConfiguration")
	BaseConfiguration appConfig;

	@Resource(name = "googleRecaptchaManager")
	GoogleRecaptchaManager googleRecaptchaManager;

	private String email;
	private String password;
	private String accountType;
	private String phoneNumber;
	private String sipPhoneNumber;
	private String sipProxy;
	private int ringSeconds;
	private boolean loggedIn;

	@PostConstruct
	public void init() {
		accountType = "SIP";
		sipProxy = appConfig.getDomain();
		ringSeconds = 120;
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
		recaptcha = googleRecaptchaManager.getRecaptcha(RECAPTCHA_KEY);
		initialavailableAreaCode();
	}

	public void login() throws ClientProtocolException, IOException,
			HttpResponseException, NotLoggedInException,
			AuthenticationException {
		if (phoneNumber == null) {
			throw new IllegalStateException("Cannot register without email.");
		}
		if (password == null) {
			throw new IllegalStateException("Cannot register without password.");
		}
		HttpPost request = new HttpPost(LOGIN_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("submit1", "Submit"));
		params.add(new BasicNameValuePair("txtDID", phoneNumber));
		params.add(new BasicNameValuePair("txtPswd", password));
		HttpEntity entity = new UrlEncodedFormEntity(params);
		request.setEntity(entity);
		HttpResponse response = httpClient.execute(request);
		HttpUtils.checkResponse(request, response, new int[] {
				HttpStatus.SC_OK, HttpStatus.SC_MOVED_TEMPORARILY }, logger);
		int statusCode = response.getStatusLine().getStatusCode();
		String location = null;
		if (response.containsHeader("Location")) {
			Header lh = response.getLastHeader("Location");
			location = lh.getValue();
			if (!location.toUpperCase().startsWith("HTTP")) {
				location = IPKALL_URL + "/" + location;
			}
		}
		request.abort();
		if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
			getInformation(location);
			loggedIn = true;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Login page return 200, which means failed.");
				if (logger.isTraceEnabled()) {
					logger.trace("HTML page: {}",
							EntityUtils.toString(response.getEntity()));
				}
			}
			loggedIn = false;
			throw new AuthenticationException();
		}
	}

	public void logout() throws ClientProtocolException, IOException {
		if (loggedIn) {
			HttpGet request = new HttpGet(LOGOUT_URL);
			httpClient.execute(request);
			request.abort();
			loggedIn = false;
		}
	}

	private void getInformation(String url) throws ClientProtocolException,
			IOException, HttpResponseException {
		String rurl = url;
		if (rurl == null) {
			rurl = INFORMATION_URL;
		}
		HttpGet request = new HttpGet(rurl);
		HttpResponse response = httpClient.execute(request);
		HttpUtils.checkResponse(request, response, logger);
		HttpEntity entity = response.getEntity();
		String charset = HttpUtils.getCharset(entity);
		InputStream in = entity.getContent();
		try {
			Document dom = Jsoup.parse(in, charset, IPKALL_URL);
			Elements es = dom.select("input[name=optType]").select("[checked]");
			if (!es.isEmpty()) {
				Element e = es.iterator().next();
				accountType = e.attr("value");
			}
			es = dom.select("input[name=txtPhone]");
			if (!es.isEmpty()) {
				Element e = es.iterator().next();
				sipPhoneNumber = e.attr("value");
			}
			es = dom.select("input[name=txtEmail]");
			if (!es.isEmpty()) {
				Element e = es.iterator().next();
				email = e.attr("value");
			}
			es = dom.select("input[name=txtSecs]");
			if (!es.isEmpty()) {
				Element e = es.iterator().next();
				String t = e.attr("value");
				try {
					ringSeconds = Integer.parseInt(t);
				} catch (NumberFormatException ex) {
					ringSeconds = 120;
				}
			}
		} finally {
			in.close();
		}
	}

	public boolean prepareRegister() throws ClientProtocolException,
			IOException, HttpResponseException {
		initRecaptcha();
		return recaptcha.isInitialized();
	}

	public void register(String areaCode, String recaptchaResponse)
			throws ClientProtocolException, IOException, HttpResponseException,
			RegistrationFailedException, RecaptchaFailedException,
			NoResponseException {
		if (areaCode != null && !availableAreaCode.contains(areaCode)) {
			throw new IllegalArgumentException("Invalid area code.");
		}
		if (recaptchaResponse == null) {
			throw new NullPointerException("recaptcha response cannot be null.");
		}
		if (email == null) {
			throw new IllegalStateException("Cannot register without email.");
		}
		if (password == null) {
			throw new IllegalStateException("Cannot register without password.");
		}
		if (recaptcha == null || !recaptcha.isInitialized()) {
			throw new IllegalStateException("Recaptcha not initial yet.");
		}
		if (areaCode == null) {
			areaCode = availableAreaCode.get(random.nextInt(availableAreaCode
					.size()));
		}
		String res = recaptcha.recaptchaChellenge(recaptchaResponse);
		HttpPost request = new HttpPost(REGISTER_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>(10);
		// cboAReaCode: area code, optType: SIP, submit1: Submit, txtEmail:
		// email,
		// txtPhone: phone number, txtProxy: domain, txtPswd: password,
		// txtSecs: 120
		params.add(new BasicNameValuePair("cboAreaCode", areaCode));
		params.add(new BasicNameValuePair("optType", "SIP"));
		params.add(new BasicNameValuePair("recaptcha_challenge_field", res));
		params.add(new BasicNameValuePair("recaptcha_response_field",
				"manual_challenge"));
		params.add(new BasicNameValuePair("submit1", "Submit"));
		params.add(new BasicNameValuePair("txtEmail", email));
		params.add(new BasicNameValuePair("txtPhone", areaCode));
		params.add(new BasicNameValuePair("txtProxy", appConfig.getDomain()));
		params.add(new BasicNameValuePair("txtPswd", password));
		params.add(new BasicNameValuePair("txtSecs", "120"));
		HttpEntity entity = new UrlEncodedFormEntity(params);
		request.setEntity(entity);
		HttpResponse response = httpClient.execute(request);
		HttpUtils.checkResponse(request, response, logger);
		entity = response.getEntity();
		String charset = HttpUtils.getCharset(entity);
		InputStream in = entity.getContent();
		try {
			Scanner scanner = new Scanner(in, charset);
			String sss = scanner.findWithinHorizon(REGISTER_SUCCESS_STRING
					+ "|" + VERIFICATION_FAILED_STRING, 2048);
			if (VERIFICATION_FAILED_STRING.equals(sss)) {
				throw new RecaptchaFailedException();
			}
			if (REGISTER_SUCCESS_STRING.equalsIgnoreCase(sss)) {
				return;
			} else {
				throw new RegistrationFailedException();
			}
		} finally {
			in.close();
		}
	}

	public void update() throws NotLoggedInException, ClientProtocolException,
			IOException, HttpResponseException, UpdateFailedException {
		if (!loggedIn) {
			throw new NotLoggedInException();
		}
		HttpPost request = new HttpPost(REGISTER_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>(7);
		params.add(new BasicNameValuePair("optType", accountType));
		params.add(new BasicNameValuePair("submit1", "Submit"));
		params.add(new BasicNameValuePair("txtEmail", email));
		params.add(new BasicNameValuePair("txtPhone", sipPhoneNumber));
		params.add(new BasicNameValuePair("txtProxy", sipProxy));
		params.add(new BasicNameValuePair("txtPswd", password));
		params.add(new BasicNameValuePair("txtSecs", Integer
				.toString(ringSeconds)));
		HttpEntity entity = new UrlEncodedFormEntity(params);
		request.setEntity(entity);
		HttpResponse response = httpClient.execute(request);
		HttpUtils.checkResponse(request, response, new int[] {
				HttpStatus.SC_OK, HttpStatus.SC_MOVED_TEMPORARILY }, logger);
		String location = null;
		if (response.containsHeader("Location")) {
			Header lh = response.getLastHeader("Location");
			location = lh.getValue();
			if (!location.toUpperCase().startsWith("HTTP")) {
				location = IPKALL_URL + "/" + location;
			}
		}
		int status = response.getStatusLine().getStatusCode();
		request.abort();
		if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
			getInformation(location);
		} else {
			throw new UpdateFailedException();
		}
	}

	public void cancel() throws NotLoggedInException, ClientProtocolException,
			IOException, HttpResponseException {
		if (!loggedIn) {
			throw new NotLoggedInException();
		}
		HttpGet request = new HttpGet(CANCEL_URL);
		HttpResponse response = httpClient.execute(request);
		HttpUtils.checkResponse(request, response, new int[] {
				HttpStatus.SC_OK, HttpStatus.SC_MOVED_TEMPORARILY }, logger);
	}

	void initRecaptcha() throws ClientProtocolException, IOException,
			HttpResponseException {
		if (!recaptcha.isInitialized()) {
			recaptcha.initRecaptcha();
		}
	}

	public File getRecaptchaImage() throws ClientProtocolException,
			IOException, HttpResponseException {
		initRecaptcha();
		return recaptcha.getImageFile();
	}

	void initialavailableAreaCode() {
		if (availableAreaCode.isEmpty()) {
			synchronized (availableAreaCode) {
				if (availableAreaCode.isEmpty()) {
					try {
						Collection<String> acs = new HashSet<String>();
						HttpGet request = new HttpGet(IPKALL_URL);
						HttpResponse response = httpClient.execute(request);
						if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
							request.abort();
							throw new HttpResponseException(response
									.getStatusLine().getStatusCode(), response
									.getStatusLine().getReasonPhrase());
						}
						HttpEntity entity = response.getEntity();
						String charset = HttpUtils.getCharset(entity);
						InputStream is = entity.getContent();
						try {
							Document dom = Jsoup.parse(is, charset, IPKALL_URL);
							Elements list = dom.select("select[id=Select1]");
							if (!list.isEmpty()) {
								Element cboAreaCode = list.iterator().next();
								Elements options = cboAreaCode.children();
								if (!options.isEmpty()) {
									for (Element e : options) {
										String v = e.attr("value");
										if (v != null && !v.isEmpty()) {
											acs.add(v);
										}
									}
								}
							}
						} finally {
							is.close();
						}
						availableAreaCode.addAll(acs);
					} catch (Throwable e) {
						if (logger.isWarnEnabled()) {
							logger.warn(
									"Get error when getting available area code, will use default value.",
									e);
						}
					}
					if (availableAreaCode.isEmpty()) {
						for (String ac : _areaCode_) {
							availableAreaCode.add(ac);
						}
					}
					Collections.sort(availableAreaCode);
				}
			}
		}
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType
	 *            the accountType to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		phoneNumber = phoneNumber.replace("-", "");
		if (phoneNumber.startsWith("+1")) {
			phoneNumber = phoneNumber.substring(2);
		}
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the sipPhoneNumber
	 */
	public String getSipPhoneNumber() {
		return sipPhoneNumber;
	}

	/**
	 * @param sipPhoneNumber
	 *            the sipPhoneNumber to set
	 */
	public void setSipPhoneNumber(String sipPhoneNumber) {
		this.sipPhoneNumber = sipPhoneNumber;
	}

	/**
	 * @return the sipProxy
	 */
	public String getSipProxy() {
		return sipProxy;
	}

	/**
	 * @param sipProxy
	 *            the sipProxy to set
	 */
	public void setSipProxy(String sipProxy) {
		this.sipProxy = sipProxy;
	}

	/**
	 * @return the ringSeconds
	 */
	public int getRingSeconds() {
		return ringSeconds;
	}

	/**
	 * @param ringSeconds
	 *            the ringSeconds to set
	 */
	public void setRingSeconds(int ringSeconds) {
		this.ringSeconds = ringSeconds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (phoneNumber != null) {
			return phoneNumber.hashCode();
		} else {
			return super.hashCode();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (this.phoneNumber == null) {
			return false;
		}
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof IPKallSession)) {
			return false;
		}
		final IPKallSession obj = (IPKallSession) other;
		return this.phoneNumber.equals(obj.getPhoneNumber());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IPKall[");
		boolean first = true;
		if (phoneNumber != null) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append("phoneNumber=").append(phoneNumber);
		}
		if (email != null) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append("email=").append(email);
		}
		sb.append("]");
		return sb.toString();
	}
}
