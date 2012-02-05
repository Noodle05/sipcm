/**
 * 
 */
package com.mycallstation.sip.util;

import gov.nist.javax.sip.address.SipUri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import javax.annotation.Resource;
import javax.sdp.Connection;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.URI;

import org.mobicents.servlet.sip.address.AddressImpl;
import org.mobicents.servlet.sip.address.SipURIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mycallstation.sip.nat.PublicIpAddressHolder;

/**
 * @author Wei Gao
 * 
 */
@Component("sipUtil")
public class SipUtil {
	private static final Logger logger = LoggerFactory.getLogger(SipUtil.class);

	@Resource(name = "publicIpAddressHolder")
	private PublicIpAddressHolder publicIpAddressHolder;

	@Resource(name = "systemConfiguration")
	private SipConfiguration appConfig;

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
		if (!appConfig.isProcessPublicIp()
				|| publicIpAddressHolder.getPublicIp() == null) {
			return;
		}
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
				forkedMessage.setContentLength(str.length());
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

	public String sipAddressToString(Address address) {
		if (address != null) {
			return address.getValue();
		} else {
			return null;
		}
	}

	public Address stringToSipAddress(String str) {
		if (str != null) {
			Address a = new AddressImpl();
			a.setValue(str);
			return a;
		} else {
			return null;
		}
	}

	public byte[] sipResponseToByteArray(SipServletResponse response) {
		if (response != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ObjectOutput out;
			try {
				out = new ObjectOutputStream(os);
				out.writeObject(response);
				out.close();
				return os.toByteArray();
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Error happened when serialize sip servlet response object.",
							e);
				}
			}
		}
		return null;
	}

	public SipServletResponse byteArrayToSipResponse(byte[] bytes) {
		if (bytes != null) {
			InputStream is = new ByteArrayInputStream(bytes);
			ObjectInput in;
			try {
				in = new ObjectInputStream(is);
				Object o = in.readObject();
				if (o instanceof SipServletResponse) {
					return (SipServletResponse) o;
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Deserialized object is not type of SipServletResponse.");
					}
				}
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Error happened when deserialize sip servlet response object.",
							e);
				}
			}
		}
		return null;
	}
}
