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
@Component("unknownMethod")
public class UnknownMethod extends AbstractMethod {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.proxy.plugins.AbstractMethod#processIncomingRequest(javax
	 * .sip.message.Request)
	 */
	@Override
	protected Response processIncomingRequest(Request request) throws Exception {
		Response response = messageFactory.createResponse(
				Response.NOT_IMPLEMENTED, request);
		return response;
	}
}
