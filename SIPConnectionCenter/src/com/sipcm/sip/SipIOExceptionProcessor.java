/**
 * 
 */
package com.sipcm.sip;

import javax.sip.IOExceptionEvent;

/**
 * @author wgao
 * 
 */
public interface SipIOExceptionProcessor {
	public void processIOException(IOExceptionEvent exceptionEvent);
}
