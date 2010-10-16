/**
 * 
 */
package com.sipcm.sip.util;

import gov.nist.javax.sip.address.SipUri;

import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ToHeader;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sipUtil")
public class SipUtil {

	public SipURI getLocationServiceKey(ToHeader toHeader) {
		Address address = toHeader.getAddress();
		URI uri = address.getURI();
		if (uri.isSipURI()) {
			SipUri su = (SipUri) uri.clone();
			su.clearPassword();
			su.clearQheaders();
			su.clearUriParms();
			return su;
		}
		return null;
	}

	public URI getCanonicalizedURI(URI uri) {
		if (uri != null && uri.isSipURI()) {
			SipUri sipUri = (SipUri) uri.clone();
			sipUri.clearUriParms();

			return sipUri;
		} else
			return uri;
	}
}
