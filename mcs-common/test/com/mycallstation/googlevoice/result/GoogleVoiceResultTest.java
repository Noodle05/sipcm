package com.mycallstation.googlevoice.result;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.Gson;
import com.mycallstation.googlevoice.setting.Phone;
import com.mycallstation.googlevoice.util.Utility;

public class GoogleVoiceResultTest {
	@Test
	public void test() {
		String str = "{\"ok\":false,\"data\":{\"code\":20}}";

		CallResult result = new CallResult(str);

		assertFalse(result.isSuccess());
		assertTrue("\"data\":{\"code\":20}".equals(result.getRawData()));
	}

	@Test
	public void test1() {
		String str = "{\"ok\":true}";

		CallResult result = new CallResult(str);

		assertTrue(result.isSuccess());
		assertNull(result.getRawData());
	}

	@Test
	public void testResult() {
		String str = "{\"ok\":true,\"data\":{\"id\":13,\"name\":\"IPKall\",\"phoneNumber\":\"+12537531783\",\"type\":1,\"policyBitmask\":3,\"smsEnabled\":false,\"formattedNumber\":\"(253) 753-1783\",\"forwardingTakenMessage\":\"+12537531783 : This number is already in use as a cell phone on another Google Voice account.\",\"wd\":{\"allDay\":false,\"times\":[{\"startTime\":\"9:00am\",\"endTime\":\"5:00pm\"}]},\"we\":{\"allDay\":false,\"times\":[{\"startTime\":\"9:00am\",\"endTime\":\"5:00pm\"}]},\"scheduleSet\":1,\"weekdayAllDay\":false,\"weekdayTimes\":[{\"startTime\":\"9:00am\",\"endTime\":\"5:00pm\"}],\"weekendAllDay\":false,\"weekendTimes\":[{\"startTime\":\"9:00am\",\"endTime\":\"5:00pm\"}],\"redirectToVoicemail\":true}}";

		EditPhoneResult result = new EditPhoneResult(str);

		assertTrue(result.isSuccess());
		assertNotNull(result.getPhone());
	}

	@Test
	public void testPhone() {
		// String str =
		// "{\"id\":13,\"name\":\"IPKall\",\"phoneNumber\":\"+12537531783\",\"type\":1,\"verified\":false,\"policyBitmask\":0,\"dEPRECATEDDisabled\":false,\"telephonyVerified\":false,\"smsEnabled\":false,\"incomingAccessNumber\":\"\",\"voicemailForwardingVerified\":false,\"behaviorOnRedirect\":1,\"carrier\":\"\",\"customOverrideState\":0,\"inVerification\":false,\"recentlyProvisionedOrDeprovisioned\":false,\"formattedNumber\":\"(253) 753-1783\",\"wd\":{\"allDay\":false,\"times\":[]},\"we\":{\"allDay\":false,\"times\":[]},\"scheduleSet\":false,\"weekdayAllDay\":false,\"weekdayTimes\":[],\"weekendAllDay\":false,\"weekendTimes\":[],\"redirectToVoicemail\":true,\"active\":true,\"enabledForOthers\":true}";
		String str = "{\"id\":13,\"name\":\"IPKall\",\"phoneNumber\":\"+12537531783\",\"type\":1,\"policyBitmask\":\"3\",\"smsEnabled\":false,\"formattedNumber\":\"(253) 753-1783\",\"forwardingTakenMessage\":\"+12537531783 : This number is already in use as a cell phone on another Google Voice account.\",\"wd\":{\"allDay\":false,\"times\":[{\"startTime\":\"9:00am\",\"endTime\":\"5:00pm\"}]},\"we\":{\"allDay\":false,\"times\":[{\"startTime\":\"9:00am\",\"endTime\":\"5:00pm\"}]},\"scheduleSet\":1,\"weekdayAllDay\":false,\"weekdayTimes\":[{\"startTime\":\"9:00am\",\"endTime\":\"5:00pm\"}],\"weekendAllDay\":false,\"weekendTimes\":[{\"startTime\":\"9:00am\",\"endTime\":\"5:00pm\"}],\"redirectToVoicemail\":true}";

		Gson gson = Utility.getGson();
		Phone phone = gson.fromJson(str, Phone.class);

		assertNotNull(phone);
	}

	@Test
	public void testConfiguration() throws SAXException, IOException,
			ParserConfigurationException {
		InputStream in = getClass().getResourceAsStream(
				"configuration_response.xml");
		Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
		InputSource is = new InputSource(reader);
		SAXParserFactory dbf = SAXParserFactory.newInstance();
		SAXParser db = dbf.newSAXParser();
		SAXHandler handler = new SAXHandler();
		db.parse(is, handler);
		String json = handler.getJson();
		assertNotNull(json);
	}

	private static class SAXHandler extends DefaultHandler {
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

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("json") && !jsonSetted) {
				inJson = true;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equalsIgnoreCase("json")) {
				inJson = false;
				jsonSetted = true;
			}
		}

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
}
