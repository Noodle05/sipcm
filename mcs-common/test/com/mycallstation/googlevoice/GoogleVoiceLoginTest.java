/**
 * 
 */
package com.mycallstation.googlevoice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.google.gson.Gson;

/**
 * @author wgao
 * 
 */
public class GoogleVoiceLoginTest {
	static final Pattern gcDataPattern = Pattern
			.compile(
					"\\s*var\\s+_gcData\\s*=\\s*(\\{.*\\});\\s*_gvRun\\(_gcData,\\s*'en_US',\\s*true\\);\\s*",
					Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNIX_LINES);

	@Test
	public void testExtractRnrSe() throws IOException {
		InputStream in = getClass().getResourceAsStream("voice_response.html");
		Document dom = Jsoup.parse(in, "UTF-8", "https://www.google.com");
		Elements scripts = dom.select("script[type$=javascript]").not(
				"script[src]");
		String str = null;
		for (Element s : scripts) {
			String t = s.html();
			Matcher m = gcDataPattern.matcher(t);
			if (m.matches()) {
				str = m.group(1);
				break;
			}
		}
		in.close();
		assertNotNull(str);
		str = str.replaceAll("<!--[^>]+-->", "");
		Gson gson = new Gson();
		RnrSeData rnr = gson.fromJson(str, RnrSeData.class);
		assertNotNull(rnr);
		assertEquals("ANlRBPTsTNUylIkzG2Sy6/8My5Q=", rnr.get_rnr_se());
		assertEquals("24230371", rnr.getV());
	}

	
}
