/**
 * 
 */
package com.sipcm.sip.proxy;

import javax.sip.ResponseEvent;

/**
 * @author wgao
 * 
 */
public interface SipResponseProcessor {
	public void processResponse(ResponseEvent responseEvent);
}
