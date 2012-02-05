/**
 * 
 */
package com.mycallstation.external.recaptcha;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
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
import com.mycallstation.googlevoice.HttpResponseException;
import com.mycallstation.util.HttpUtils;

/**
 * @author Wei Gao
 * 
 */
@Component("googleRecaptcha")
@Scope("prototype")
public class GoogleRecaptcha {
	static final String RECAPTCHA_BASE_URL = "http://api.recaptcha.net";
	static final String RECAPTCHA_URL = RECAPTCHA_BASE_URL + "/noscript";

	static final String TEMP_FILE_PRE = "rcp_";
	static final String TEMP_FILE_SUF = ".tmp";

	private static final Logger logger = LoggerFactory
			.getLogger(GoogleRecaptcha.class);

	@Resource(name = "httpConnectionManager")
	ClientConnectionManager connMgr;

	@Resource(name = "systemConfiguration")
	BaseConfiguration appConfig;

	private HttpClient httpClient;

	private String key;
	private String recaptchaChallengeField;
	private File imageFile;
	private int imageWidth;
	private int imageHeight;
	private String imageType;
	private volatile boolean initialized = false;

	@PostConstruct
	public void init() {
		HttpParams params = new BasicHttpParams();
		int connTimeout = appConfig.getHttpClientConnectionTimeout();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				connTimeout);
		DefaultHttpClient client = new DefaultHttpClient(connMgr, params);
		client.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				request.setHeader("User-agent", McsConstant.USER_AGENT);
			}
		});
		httpClient = client;
	}

	public void initRecaptcha() throws ClientProtocolException, IOException,
			HttpResponseException {
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing recaptcha.");
		}
		HttpGet request = new HttpGet(prepareUrl());

		HttpResponse response = httpClient.execute(request);
		HttpUtils.checkResponse(request, response, logger);
		HttpEntity entity = response.getEntity();
		String charset = HttpUtils.getCharset(entity);
		initRecaptcha(EntityUtils.toString(entity), charset);
	}

	private void initRecaptcha(String str, String charset) throws IOException,
			HttpResponseException {
		deleteImageFile();
		imageWidth = 0;
		imageHeight = 0;
		imageType = null;
		String imageUrl = null;
		Document dom = Jsoup.parse(str, RECAPTCHA_BASE_URL);
		Elements list = dom.select("input[id=recaptcha_challenge_field]");
		if (list != null) {
			Element e = list.first();
			recaptchaChallengeField = e.attr("value");
		}
		list = dom.select("img");
		if (list != null) {
			Element e = list.first();
			imageUrl = e.absUrl("src");
			String ss = e.attr("width");
			if (!ss.isEmpty()) {
				try {
					imageWidth = Integer.valueOf(ss);
				} catch (NumberFormatException ex) {
					imageWidth = 0;
				}
			}
			ss = e.attr("height");
			if (!ss.isEmpty()) {
				try {
					imageHeight = Integer.valueOf(ss);
				} catch (NumberFormatException ex) {
					imageHeight = 0;
				}
			}
		}
		imageFile = File.createTempFile(TEMP_FILE_PRE, TEMP_FILE_SUF,
				appConfig.getTemperoryFolder());
		imageFile.deleteOnExit();
		HttpGet request = new HttpGet(imageUrl);
		HttpResponse response = httpClient.execute(request);
		HttpUtils.checkResponse(request, response, logger);
		HttpEntity entity = response.getEntity();
		if (entity.getContentType() != null) {
			HeaderElement values[] = entity.getContentType().getElements();
			if (values.length > 0) {
				imageType = values[0].getName();
			}
		}
		if (imageType == null) {
			throw new RuntimeException("Cannot get image type.");
		}
		InputStream is = response.getEntity().getContent();
		OutputStream os = new FileOutputStream(imageFile);
		try {
			if (getImageWidth() == 0 || getImageHeight() == 0) {
				BufferedImage img = ImageIO.read(is);
				imageWidth = img.getWidth();
				imageHeight = img.getHeight();
				ImageIO.write(img, "jpeg", os);
				imageType = "image/jpeg";
			} else {
				byte[] d = new byte[512];
				int r = 0;
				while ((r = is.read(d)) > 0) {
					os.write(d, 0, r);
				}
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// ignore.
			}
			try {
				os.close();
			} catch (IOException e) {
				// ignore.
			}
		}
		initialized = true;
	}

	public String recaptchaChellenge(String recaptchaResponse)
			throws ClientProtocolException, IOException, HttpResponseException,
			RecaptchaFailedException, NoResponseException {
		if (recaptchaResponse == null) {
			throw new NullPointerException("Response cannot be null.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Trying to chellenge recaptcha with response: \"{}\"",
					recaptchaResponse);
		}
		deleteImageFile();
		HttpPost request = new HttpPost(prepareUrl());
		List<NameValuePair> ps = new ArrayList<NameValuePair>(3);
		ps.add(new BasicNameValuePair("recaptcha_challenge_field",
				getRecaptchaChallengeField()));
		ps.add(new BasicNameValuePair("recaptcha_response_field",
				recaptchaResponse));
		ps.add(new BasicNameValuePair("submit", "I'm a human"));
		HttpEntity en = new UrlEncodedFormEntity(ps);
		request.setEntity(en);
		if (logger.isTraceEnabled()) {
			logger.trace("Sending http request with response to: {}",
					request.getURI());
		}
		HttpResponse response = httpClient.execute(request);
		HttpUtils.checkResponse(request, response, logger);
		HttpEntity entity = response.getEntity();
		String charset = HttpUtils.getCharset(entity);
		String str = EntityUtils.toString(entity);
		boolean success = false;
		Scanner sc = new Scanner(str);
		if (sc.findWithinHorizon("Your answer was correct", 1024) != null) {
			success = true;
		}
		if (success) {
			if (logger.isTraceEnabled()) {
				logger.trace("Answer correct, getting response field.");
			}
			Document dom = Jsoup.parse(str, "http://api.recaptcha.net");

			Elements list = dom.select("textarea");
			if (list != null && !list.isEmpty()) {
				Element e = list.first();
				String ret = e.text();
				if (logger.isTraceEnabled()) {
					logger.trace("Get response: {}", ret);
				}
				return ret;
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("I cannot find response field from http response.");
				}
				throw new NoResponseException();
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Answer was wrong, reinitial recaptcha and throw exception.");
			}
			initRecaptcha(str, charset);
			throw new RecaptchaFailedException();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		deleteImageFile();
		super.finalize();
	}

	private void deleteImageFile() {
		if (imageFile != null) {
			try {
				imageFile.delete();
			} catch (Throwable e) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Error happened when delete image file, it may not exist any more.",
							e);
				}
			} finally {
				imageFile = null;
			}
		}
	}

	private String prepareUrl() {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>(1);
		qparams.add(new BasicNameValuePair("k", key));
		return HttpUtils.prepareGetUrl(RECAPTCHA_URL, qparams);
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the recaptchaChallengeField
	 */
	public String getRecaptchaChallengeField() {
		return recaptchaChallengeField;
	}

	/**
	 * @return the imageFile
	 */
	public File getImageFile() {
		return imageFile;
	}

	/**
	 * @return the imageWidth
	 */
	public int getImageWidth() {
		return imageWidth;
	}

	/**
	 * @return the imageHeight
	 */
	public int getImageHeight() {
		return imageHeight;
	}

	/**
	 * @return the imageType
	 */
	public String getImageType() {
		return imageType;
	}

	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}
}
