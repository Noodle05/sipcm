/**
 * 
 */
package com.sipcm.sip.util;

import gov.nist.javax.sip.address.SipUri;

import javax.servlet.sip.URI;

import org.mobicents.servlet.sip.address.SipURIImpl;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sipUtil")
public class SipUtil {
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
}
