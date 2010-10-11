/**
 * 
 */
package com.sipcm.sip;

import javax.sip.RequestEvent;

/**
 * @author wgao
 * 
 */
public interface SipRequestProcessor {
	public void processRequest(RequestEvent requestEvent);
}
