/**
 * 
 */
package com.sipcm.sip.proxy.plugins;

import javax.sip.message.Request;
import javax.sip.message.Response;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("ackMethod")
public class AckMethod extends AbstractMethod {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.proxy.plugins.AbstractMethodPlugin#processIncomingRequest
	 * (javax.sip.message.Request)
	 */
	@Override
	protected Response processIncomingRequest(Request request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
