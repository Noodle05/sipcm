/**
 * 
 */
package com.sipcm.googlevoice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.common.AuthenticationException;

/**
 * @author wgao
 */
@Component("googleVoiceSession")
@Scope("prototype")
public class GoogleVoiceSession {
	private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.A.B.C Safari/525.13";

	private static final String loginUrl = "https://www.google.com/accounts/ClientLogin";
	private static final String baseUrl = "https://www.google.com/voice/";
	private static final String callUrl = "https://www.google.com/voice/call/connect/";
	private static final String cancelUrl = "https://www.google.com/voice/call/cancel/";

	private static final Pattern authPattern = Pattern.compile("^Auth=(.*)$");
	private static final Pattern rnr_sePattern = Pattern
			.compile("^\\s*'_rnr_se':\\s+'(.*)',\\s*$");
	private static final Pattern errorPattern = Pattern.compile("^Error=(.*)$");
	private static final Pattern captchaTokenPattern = Pattern
			.compile("^CaptchaToken=(.*)$");
	private static final Pattern captchaUrlPattern = Pattern
			.compile("^CaptchaUrl=(.*)$");

	private String username;
	private String password;
	private HttpClient httpClient;
	private String authToken;
	private String rnrSe;
	private int maxRetry = 1;

	@Resource(name = "googleVoiceManager")
	private GoogleVoiceManager manager;

	public GoogleVoiceSession(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void init() {
		HttpParams params = new BasicHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(
				manager.getConnectionManager(), params);
		client.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				if (!request.containsHeader("Authentication")
						&& authToken != null) {
					request.addHeader("Authorization", "GoogleLogin auth="
							+ authToken);
				}
				request.setHeader("User-agent", USER_AGENT);
			}
		});
		httpClient = client;
	}

	public void login() throws ClientProtocolException, IOException,
			AuthenticationException, HttpResponseException {
		HttpPost login = new HttpPost(loginUrl);
		List<NameValuePair> ps = new ArrayList<NameValuePair>();
		ps.add(new BasicNameValuePair("accountType", "GOOGLE"));
		ps.add(new BasicNameValuePair("Email", username));
		ps.add(new BasicNameValuePair("Passwd", password));
		ps.add(new BasicNameValuePair("service", "grandcentral"));
		ps.add(new BasicNameValuePair("source", "sipcm-sipcm-1.0"));
		HttpEntity oe = new UrlEncodedFormEntity(ps);
		login.setEntity(oe);
		HttpResponse response = httpClient.execute(login);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			try {
				authToken = null;
				String tmp = null;
				while ((tmp = reader.readLine()) != null) {
					Matcher m = authPattern.matcher(tmp);
					if (m.matches()) {
						authToken = m.group(1);
						break;
					}
				}
			} finally {
				reader.close();
			}
			if (authToken != null) {
				HttpGet base = new HttpGet(baseUrl);
				response = httpClient.execute(base);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					reader = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent()));
					try {
						String tmp = null;
						while ((tmp = reader.readLine()) != null) {
							Matcher m = rnr_sePattern.matcher(tmp);
							if (m.matches()) {
								rnrSe = m.group(1);
								break;
							}
						}
					} finally {
						reader.close();
					}
					if (rnrSe == null) {
						throw new NoRnrSeException();
					}
				} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
					parseError(response.getEntity());
				} else {
					throw new HttpResponseException(response.getStatusLine()
							.getStatusCode(),
							"Error happened when parse rnr_se");
				}
			} else {
				throw new NoAuthTokenException();
			}
		} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
			parseError(response.getEntity());
		} else {
			throw new HttpResponseException(response.getStatusLine()
					.getStatusCode(), "Error happened when parse auth tokens");
		}
	}

	public void call(String origination, String destination, String phoneType)
			throws ClientProtocolException, IOException, HttpResponseException,
			AuthenticationException {
		HttpPost callPost = new HttpPost(callUrl);
		List<NameValuePair> ps = new ArrayList<NameValuePair>();
		ps.add(new BasicNameValuePair("outgoingNumber", destination));
		ps.add(new BasicNameValuePair("forwardingNumber", origination));
		ps.add(new BasicNameValuePair("subscriberNumber", "undefined"));
		ps.add(new BasicNameValuePair("phoneType", phoneType));
		ps.add(new BasicNameValuePair("remember", "0"));
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		callMethod(callPost, 0);
	}

	public void cancel() throws ClientProtocolException, IOException,
			HttpResponseException, AuthenticationException {
		HttpPost callPost = new HttpPost(cancelUrl);
		List<NameValuePair> ps = new ArrayList<NameValuePair>();
		ps.add(new BasicNameValuePair("outgoingNumber", "undefined"));
		ps.add(new BasicNameValuePair("forwardingNumber", "undefined"));
		ps.add(new BasicNameValuePair("cancelType", "C2C"));
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		callMethod(callPost, 0);
	}

	private void callMethod(HttpUriRequest request, int retry)
			throws ClientProtocolException, IOException, HttpResponseException,
			AuthenticationException {
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
			if (retry < maxRetry) {
				login();
				callMethod(request, retry++);
			} else {
				parseError(response.getEntity());
			}
		} else {
			throw new HttpResponseException(response.getStatusLine()
					.getStatusCode(), "Error happened when parse auth tokens");
		}
	}

	private void parseError(HttpEntity entity)
			throws GoogleAuthenticationException, IllegalStateException,
			IOException, HttpResponseException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				entity.getContent()));
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
			GoogleAuthenticationException.throwProperException(errorCode,
					captchaToken, captchaUrl);
		} else {
			throw new HttpResponseException(HttpStatus.SC_FORBIDDEN,
					"Error happened when parse error page. Cannot find error code.");
		}
	}
}
