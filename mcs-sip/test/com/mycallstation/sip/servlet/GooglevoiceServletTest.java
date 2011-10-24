/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;

import javax.sdp.Attribute;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;

import org.junit.Test;

/**
 * @author wgao
 * 
 */
public class GooglevoiceServletTest {
	public static String msg2 = "v=0\r\n"
			+ "o=root 20110 20110 IN IP4 66.54.140.46\r\n" + "s=session\r\n"
			+ "c=IN IP4 66.54.140.46\r\n" + "t=0 0\r\n"
			+ "m=audio 19286 RTP/AVP 0 8 3 18 101\r\n"
			+ "a=rtpmap:0 PCMU/8000\r\n" + "a=rtpmap:8 PCMA/8000\r\n"
			+ "a=rtpmap:3 GSM/8000\r\n" + "a=rtpmap:18 G729/8000\r\n"
			+ "a=fmtp:18 annexb=no\r\n"
			+ "a=rtpmap:101 telephone-event/8000\r\n" + "a=fmtp:101 0-16\r\n"
			+ "a=silenceSupp:off - - - -\r\n" + "a=ptime:20\r\n"
			+ "a=sendrecv\r\n";

	public static String msg1 = "v=0\r\n"
			+ "o=- 421600372 421600372 IN IP4 99.185.42.159\r\n" + "s=-\r\n"
			+ "c=IN IP4 99.185.42.159\r\n" + "t=0 0\r\n"
			+ "m=audio 16420 RTP/AVP 18 0 2 4 8 96 97 98 100 101\r\n"
			+ "a=rtpmap:18 G729a/8000\r\n" + "a=rtpmap:0 PCMU/8000\r\n"
			+ "a=rtpmap:2 G726-32/8000\r\n" + "a=rtpmap:4 G723/8000\r\n"
			+ "a=rtpmap:8 PCMA/8000\r\n" + "a=rtpmap:96 G726-40/8000\r\n"
			+ "a=rtpmap:97 G726-24/8000\r\n" + "a=rtpmap:98 G726-16/8000\r\n"
			+ "a=rtpmap:100 NSE/8000\r\n" + "a=fmtp:100 192-193\r\n"
			+ "a=rtpmap:101 telephone-event/8000\r\n" + "a=fmtp:101 0-15\r\n"
			+ "a=ptime:30\r\n" + "a=sendrecv\r\n";

	@Test
	public void test() {
		try {
			findCommonCodec(msg1, msg2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SessionDescription[] findCommonCodec(String msg1, String msg2)
			throws IOException, SdpException {
		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdp1 = null;
		SessionDescription sdp2 = null;
		sdp1 = sdpFactory.createSessionDescription(msg1);
		sdp2 = sdpFactory.createSessionDescription(msg2);
		if (sdp1 != null && sdp2 != null) {
			@SuppressWarnings("unchecked")
			Vector<MediaDescription> mds1 = sdp1.getMediaDescriptions(false);
			@SuppressWarnings("unchecked")
			Vector<MediaDescription> mds2 = sdp2.getMediaDescriptions(false);
			MediaDescription md1 = mds1.firstElement();
			MediaDescription md2 = mds2.firstElement();
			@SuppressWarnings("unchecked")
			Vector<String> codecs1 = md1.getMedia().getMediaFormats(false);
			@SuppressWarnings("unchecked")
			Vector<String> codecs2 = md2.getMedia().getMediaFormats(false);
			Integer codec = null;
			for (String c : codecs1) {
				if (codecs2.contains(c)) {
					codec = Integer.parseInt(c);
					break;
				}
			}
			processMediaDescription(md1, codec);
			processMediaDescription(md2, codec);
		}
		SessionDescription[] ret = new SessionDescription[] { sdp1, sdp2 };
		return ret;
	}

	private void processMediaDescription(MediaDescription md, int codec)
			throws SdpParseException {
		@SuppressWarnings("unchecked")
		Vector<String> codecs = md.getMedia().getMediaFormats(false);
		Iterator<String> itec = codecs.iterator();
		while (itec.hasNext()) {
			int c = Integer.parseInt(itec.next());
			if (c < 100 && c != codec) {
				itec.remove();
			}
		}
		@SuppressWarnings("unchecked")
		Vector<Attribute> attrs = md.getAttributes(false);
		Iterator<Attribute> itea = attrs.iterator();
		while (itea.hasNext()) {
			Attribute a = itea.next();
			if ("rtpmap".equals(a.getName()) || "fmtp".equals(a.getName())) {
				if (a.getValue() != null) {
					Matcher m = GoogleVoiceServlet.codecPattern.matcher(a
							.getValue());
					if (m.matches()) {
						int c = Integer.parseInt(m.group(1));
						if (c < 100 && c != codec) {
							itea.remove();
						}
					}
				}
			}
		}
	}

}
