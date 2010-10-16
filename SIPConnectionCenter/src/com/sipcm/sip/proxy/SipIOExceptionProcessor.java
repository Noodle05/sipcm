/**
 * 
 */
package com.sipcm.sip.proxy;

import javax.sip.IOExceptionEvent;

/**
 * @author wgao
 * 
 */
public interface SipIOExceptionProcessor {
	public void processIOException(IOExceptionEvent exceptionEvent);
}
