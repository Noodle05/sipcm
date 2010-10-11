/**
 * 
 */
package com.sipcm.sip;

import javax.sip.ResponseEvent;

/**
 * @author wgao
 * 
 */
public interface SipResponseProcessor {
	public void processResponse(ResponseEvent responseEvent);
}
