/**
 * 
 */
package com.mycallstation.external.recaptcha;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

import com.mycallstation.common.TestConfiguration;
import com.mycallstation.googlevoice.HttpResponseException;

/**
 * @author wgao
 * 
 */
public class RecaptchaParseTest {
	static final Pattern recaptchaChallengeFieldPattern = Pattern
			.compile(
					"<input\\s+type=\"hidden\"\\s+name=\"recaptcha_chanllenge_field\"\\s+id=\"recaptcha_chanllenge_field\"\\s+value=\"(\\w+)\">",
					Pattern.MULTILINE + Pattern.DOTALL + Pattern.UNIX_LINES);

	@Test
	public void testParseResponse() throws IOException {
		InputStream is = getClass().getResourceAsStream(
				"recaptcha_response.html");

		String recaptchaChallengeField = null;
		String imageUrl = null;
		try {
			Document dom = Jsoup.parse(is, HTTP.DEFAULT_CONTENT_CHARSET,
					"http://api.recaptcha.net");

			Elements list = dom.select("input[id=recaptcha_challenge_field]");
			if (list != null && !list.isEmpty()) {
				Element e = list.first();
				recaptchaChallengeField = e.attr("value");
			}
			list = dom.select("img");
			if (list != null && !list.isEmpty()) {
				Element e = list.first();
				imageUrl = e.absUrl("src");
			}
		} finally {
			is.close();
		}
		assertEquals(
				"03AHJ_VusfjKOAfXAwfa3033vU3bUs5gqty4TW3JWjFpFb3TH3kl7Dj-HGe_R5epFd_rB4aebhuu1HR9HqIKz7WwbzfL3W_5OlcmYpevHat9QtAsQ32fQojs-2-OTg7kj7d7IUAGh0mCO8vwYTHQ2b0XmrhSXXBoABvA",
				recaptchaChallengeField);
		assertEquals(
				"http://api.recaptcha.net/image?c=03AHJ_VusfjKOAfXAwfa3033vU3bUs5gqty4TW3JWjFpFb3TH3kl7Dj-HGe_R5epFd_rB4aebhuu1HR9HqIKz7WwbzfL3W_5OlcmYpevHat9QtAsQ32fQojs-2-OTg7kj7d7IUAGh0mCO8vwYTHQ2b0XmrhSXXBoABvA",
				imageUrl);
	}

	@Test
	public void testParseSuccess() throws IOException {
		InputStream is = getClass().getResourceAsStream(
				"recaptcha_success.html");

		String recaptchaChallengeField = null;
		try {
			Document dom = Jsoup.parse(is, HTTP.DEFAULT_CONTENT_CHARSET,
					"http://api.recaptcha.net");

			Elements list = dom.select("textarea");
			if (list != null && !list.isEmpty()) {
				Element e = list.first();
				recaptchaChallengeField = e.text();
			}
		} finally {
			is.close();
		}
		assertEquals(
				"03AHJ_VuvfM-tQSwlobw4lGfdH7-yKjt91JjBKsgstKbOd8Pfb4gQLdzbocBdV2DgeP2brYqo9gHdwSdtBfpDZcbA6rJmiH-3z79o5AghwfYMp3Z1lMZKYsUg",
				recaptchaChallengeField);
	}

	@Test
	public void testFailedResult() throws IOException {
		InputStream is = getClass().getResourceAsStream(
				"recaptcha_response.html");

		String recaptchaChallengeField = null;
		String imageUrl = null;
		boolean success = false;
		try {
			is.mark(1024);
			Scanner sc = new Scanner(is);
			if (sc.findWithinHorizon("Your answer was correct", 1024) != null) {
				success = true;
			}
			assertFalse(success);
			is.reset();
			Document dom = Jsoup.parse(is, HTTP.DEFAULT_CONTENT_CHARSET,
					"http://api.recaptcha.net");

			Elements list = dom.select("input[id=recaptcha_challenge_field]");
			if (list != null && !list.isEmpty()) {
				Element e = list.first();
				recaptchaChallengeField = e.attr("value");
			}
			list = dom.select("img");
			if (list != null && !list.isEmpty()) {
				Element e = list.first();
				imageUrl = e.absUrl("src");
			}
		} finally {
			is.close();
		}
		assertEquals(
				"03AHJ_VusfjKOAfXAwfa3033vU3bUs5gqty4TW3JWjFpFb3TH3kl7Dj-HGe_R5epFd_rB4aebhuu1HR9HqIKz7WwbzfL3W_5OlcmYpevHat9QtAsQ32fQojs-2-OTg7kj7d7IUAGh0mCO8vwYTHQ2b0XmrhSXXBoABvA",
				recaptchaChallengeField);
		assertEquals(
				"http://api.recaptcha.net/image?c=03AHJ_VusfjKOAfXAwfa3033vU3bUs5gqty4TW3JWjFpFb3TH3kl7Dj-HGe_R5epFd_rB4aebhuu1HR9HqIKz7WwbzfL3W_5OlcmYpevHat9QtAsQ32fQojs-2-OTg7kj7d7IUAGh0mCO8vwYTHQ2b0XmrhSXXBoABvA",
				imageUrl);
	}

	@Test
	public void testSuccessResult() throws IOException {
		InputStream is = getClass().getResourceAsStream(
				"recaptcha_success.html");

		String recaptchaChallengeField = null;
		boolean success = false;
		try {
			is.mark(1024);
			Scanner sc = new Scanner(is);
			if (sc.findWithinHorizon("Your answer was correct", 1024) != null) {
				success = true;
			}

			assertTrue(success);
			is.reset();
			Document dom = Jsoup.parse(is, HTTP.DEFAULT_CONTENT_CHARSET,
					"http://api.recaptcha.net");

			Elements list = dom.select("textarea");
			if (list != null && !list.isEmpty()) {
				Element e = list.first();
				recaptchaChallengeField = e.text();
			}
		} finally {
			is.close();
		}
		assertEquals(
				"03AHJ_VuvfM-tQSwlobw4lGfdH7-yKjt91JjBKsgstKbOd8Pfb4gQLdzbocBdV2DgeP2brYqo9gHdwSdtBfpDZcbA6rJmiH-3z79o5AghwfYMp3Z1lMZKYsUg",
				recaptchaChallengeField);
	}

	@Ignore
	@Test(expected = RecaptchaFailedException.class)
	public void testGoogleRecaptcha() throws ClientProtocolException,
			IOException, HttpResponseException, RecaptchaFailedException,
			NoResponseException {
		GoogleRecaptcha recaptcha = new GoogleRecaptcha();
		recaptcha.connMgr = new ThreadSafeClientConnManager();
		recaptcha.appConfig = new TestConfiguration();
		recaptcha.init();
		recaptcha.setKey("6LfigQEAAAAAAGotL5h_MF0CAySSkSnLb_H_bFaP");
		recaptcha.initRecaptcha();
		assertTrue(recaptcha.isInitialized());
		recaptcha.recaptchaChellenge("aaa");
		fail();
	}
}
