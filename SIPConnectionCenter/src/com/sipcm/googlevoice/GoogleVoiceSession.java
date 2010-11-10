/**
 * 
 */
package com.sipcm.googlevoice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.common.AuthenticationException;

/**
 * @author wgao
 */
@Component("googleVoiceSession")
@Scope("prototype")
public class GoogleVoiceSession implements Serializable {
	private static final Logger logger = LoggerFactory
			.getLogger(GoogleVoiceSession.class);

	private static final long serialVersionUID = 727761632230756155L;

	private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.A.B.C Safari/525.13";

	private static final String loginPageUrl = "https://www.google.com/accounts/ServiceLogin";
	private static final String loginUrl = "https://www.google.com/accounts/ServiceLoginAuth";
	private static final String continueUrl = "https://www.google.com/voice/account/signin";
	private static final String callUrl = "https://www.google.com/voice/call/connect/";
	private static final String cancelUrl = "https://www.google.com/voice/call/cancel/";

	private static Pattern galxPattern = Pattern.compile(
			".*name=\"GALX\"\\s*value=\"([^\"]*)\".*", Pattern.DOTALL
					+ Pattern.CASE_INSENSITIVE);

	private static final Pattern resultPattern = Pattern.compile(
			"^\\{\"ok\"\\s*\\:\\s*(false|true).*\\}$", Pattern.DOTALL
					+ Pattern.CASE_INSENSITIVE);

	private static final Pattern rnr_sePattern = Pattern
			.compile("^\\s*'_rnr_se':\\s*'(.*)',\\s*$");
	private static final Pattern errorPattern = Pattern.compile("^Error=(.*)$");
	private static final Pattern captchaTokenPattern = Pattern
			.compile("^CaptchaToken=(.*)$");
	private static final Pattern captchaUrlPattern = Pattern
			.compile("^CaptchaUrl=(.*)$");

	private String username;
	private String password;
	private String myNumber;
	private HttpClient httpClient;
	private String rnrSe;
	private int maxRetry = 1;
	private boolean cancelCall;

	@Resource(name = "googleVoiceManager")
	private GoogleVoiceManager manager;

	public void init() {
		HttpParams params = new BasicHttpParams();
		DefaultHttpClient client = new DefaultHttpClient(
				manager.getConnectionManager(), params);
		client.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				request.setHeader("User-agent", USER_AGENT);
			}
		});
		httpClient = client;
	}

	public void login() throws ClientProtocolException, IOException,
			AuthenticationException, HttpResponseException {
		if (logger.isDebugEnabled()) {
			logger.debug("Trying to login.");
		}
		HttpGet loginPage = new HttpGet(loginPageUrl);
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to get galx.");
		}
		HttpResponse response = httpClient.execute(loginPage);
		String loginBody = response.getEntity() == null ? null : EntityUtils
				.toString(response.getEntity());
		String galx = null;
		if (loginBody != null) {
			Matcher m = galxPattern.matcher(loginBody);
			if (m.matches()) {
				galx = m.group(1);
				if (logger.isTraceEnabled()) {
					logger.trace("Get galx: {}", galx);
				}
			}
		}
		if (galx == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("I cannot find galx for this login.");
			}
			throw new NoAuthTokenException();
		}
		HttpPost login = new HttpPost(loginUrl);
		List<NameValuePair> ps = new ArrayList<NameValuePair>();
		ps.add(new BasicNameValuePair("Email", username));
		ps.add(new BasicNameValuePair("Passwd", password));
		ps.add(new BasicNameValuePair("continue", continueUrl));
		ps.add(new BasicNameValuePair("GALX", galx));
		HttpEntity oe = new UrlEncodedFormEntity(ps);
		login.setEntity(oe);
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to get rnrse token.");
		}
		HttpRequestBase request = login;
		response = httpClient.execute(request);
		while (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
				|| response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY
				|| response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {
			Header locationHeader = response.getFirstHeader("location");
			if (locationHeader == null) {
				request.abort();
				// got a redirect response, but no location header
				throw new ClientProtocolException("Received redirect response "
						+ response.getStatusLine() + " but no location header");
			}
			String location = locationHeader.getValue();
			request.abort();
			request = new HttpGet(location);
			response = httpClient.execute(request);
		}
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
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
				if (logger.isDebugEnabled()) {
					logger.debug("Cannot find rnr.");
				}
				throw new NoRnrSeException();
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Find rnr: {}", rnrSe);
			}
		} else {
			request.abort();
			if (logger.isDebugEnabled()) {
				logger.debug("Login page return error {}", response
						.getStatusLine().getStatusCode());
			}
			throw new HttpResponseException(response.getStatusLine()
					.getStatusCode(), "Error happened during login.");
		}
	}

	public boolean call(String destination, String phoneType)
			throws ClientProtocolException, IOException, HttpResponseException,
			AuthenticationException {
		if (!cancelCall) {
			HttpPost callPost = new HttpPost(callUrl);
			List<NameValuePair> ps = new ArrayList<NameValuePair>();
			ps.add(new BasicNameValuePair("outgoingNumber", destination));
			ps.add(new BasicNameValuePair("forwardingNumber", myNumber));
			ps.add(new BasicNameValuePair("subscriberNumber", "undefined"));
			ps.add(new BasicNameValuePair("phoneType", phoneType));
			ps.add(new BasicNameValuePair("remember", "0"));
			ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
			HttpEntity en = new UrlEncodedFormEntity(ps);
			callPost.setEntity(en);
			if (logger.isTraceEnabled()) {
				logger.trace(
						"Calling \"call\" method with target number {}, callback number {}, phone type {}",
						new Object[] { destination, myNumber, phoneType });
			}
			return callMethod(callPost, 0);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("This call already cancelled.");
			}
			return false;
		}
	}

	public boolean cancel() throws ClientProtocolException, IOException,
			HttpResponseException, AuthenticationException {
		if (logger.isDebugEnabled()) {
			logger.debug("Cancelling call.");
		}
		cancelCall = true;
		HttpPost callPost = new HttpPost(cancelUrl);
		List<NameValuePair> ps = new ArrayList<NameValuePair>();
		ps.add(new BasicNameValuePair("outgoingNumber", "undefined"));
		ps.add(new BasicNameValuePair("forwardingNumber", "undefined"));
		ps.add(new BasicNameValuePair("cancelType", "C2C"));
		ps.add(new BasicNameValuePair("_rnr_se", rnrSe));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		callPost.setEntity(en);
		if (logger.isTraceEnabled()) {
			logger.trace("Call \"cancel\" method.");
		}
		return callMethod(callPost, 0);
	}

	private boolean callMethod(HttpUriRequest request, int retry)
			throws ClientProtocolException, IOException, HttpResponseException,
			AuthenticationException {
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
				return callMethod(request, retry++);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Call failed, parsing failed reason.");
				}
				parseError(response.getEntity());
			}
		} else if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			if (logger.isDebugEnabled()) {
				logger.debug("Call method failed with status code: {}",
						response.getStatusLine().getStatusCode());
			}
			request.abort();
			throw new HttpResponseException(response.getStatusLine()
					.getStatusCode(), "Error happened when parse auth tokens");
		}
		String body = EntityUtils.toString(response.getEntity());
		if (logger.isTraceEnabled()) {
			logger.trace("Method return body: {}", body);
		}
		Matcher m = resultPattern.matcher(body);
		if (m.matches()) {
			if ("true".equalsIgnoreCase(m.group(1))) {
				if (logger.isTraceEnabled()) {
					logger.trace("Method return \"ok\" : true. Success.");
				}
				return true;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Call failed.");
		}
		return false;
	}

	private void parseError(HttpEntity entity)
			throws GoogleAuthenticationException, IllegalStateException,
			IOException, HttpResponseException {
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent()));
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

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMyNumber(String myNumber) {
		this.myNumber = myNumber;
	}

	public String getMyNumber() {
		return myNumber;
	}
}
