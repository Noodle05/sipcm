/**
 * 
 */
package com.sipcm.sip.proxy;

import javax.sip.TimeoutEvent;

/**
 * @author wgao
 * 
 */
public interface SipTimeoutProcessor {
	public void processTimeout(TimeoutEvent timeoutEvent);
}
