/**
 * 
 */
package com.sipcm.sip.util;

import gov.nist.javax.sip.address.SipUri;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import javax.annotation.Resource;
import javax.sdp.Connection;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.URI;

import org.mobicents.servlet.sip.address.SipURIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sipcm.sip.nat.PublicIpAddressHolder;

/**
 * @author wgao
 * 
 */
@Component("sipUtil")
public class SipUtil {
	private static final Logger logger = LoggerFactory.getLogger(SipUtil.class);

	@Resource(name = "publicIpAddressHolder")
	private PublicIpAddressHolder publicIpAddressHolder;

	public URI getCanonicalizedURI(URI uri) {
		if (uri instanceof SipURIImpl) {
			SipURIImpl su = (SipURIImpl) uri.clone();
			javax.sip.address.URI ur = su.getURI();
			if (ur instanceof SipUri) {
				SipUri sur = (SipUri) ur;
				sur.clearUriParms();
				return su;
			}
		}
		return uri;
	}

	public void processingAddressInSDP(SipServletMessage forkedMessage,
			SipServletMessage originalMessage) {
		try {
			byte[] bc = forkedMessage.getRawContent();
			if (bc == null || bc.length <= 0) {
				return;
			}
			String encode = forkedMessage.getCharacterEncoding();
			String contentType = forkedMessage.getContentType();
			if (encode == null) {
				encode = "UTF-8";
			}
			String str;
			try {
				str = new String(bc, encode);
			} catch (UnsupportedEncodingException e) {
				str = new String(bc);
			}
			if (str.isEmpty()) {
				return;
			}
			SdpFactory sdpFactory = SdpFactory.getInstance();
			SessionDescription sd = sdpFactory.createSessionDescription(str);
			Connection c = sd.getConnection();
			if (c == null) {
				return;
			}
			InetAddress ip = InetAddress.getByName(c.getAddress());
			if (ip.isSiteLocalAddress()) {
				InetAddress oip = InetAddress.getByName(originalMessage
						.getInitialRemoteAddr());
				if (oip.isSiteLocalAddress() || oip.isLoopbackAddress()) {
					oip = publicIpAddressHolder.getPublicIp();
				}
				c.setAddress(oip.getHostAddress());
				str = sd.toString();
				forkedMessage.setContent(str, contentType);
			}
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error happened when processing IP address in sdp, "
						+ "please turn on debug to see detail error message.");
				if (logger.isDebugEnabled()) {
					logger.debug("Detail exception stack:", e);
				}
			}
		}
	}
}
