/**
 * 
 */
package com.sipcm.sip.proxy.plugins;

import javax.sip.header.AllowHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("unsupportedMethod")
public class UnsupportedMethod extends AbstractMethod {
	public static final String allowMethods = "REGISTER, INVITE, ACK, BYE, INFO, OPTIONS, CANCEL";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.proxy.plugins.AbstractMethodPlugin#processIncomingRequest
	 * (javax.sip.message.Request)
	 */
	@Override
	protected Response processIncomingRequest(Request request) throws Exception {
		Response response = messageFactory.createResponse(
				Response.METHOD_NOT_ALLOWED, request);
		Header allowHeader = headerFactory.createHeader(AllowHeader.NAME,
				allowMethods);
		response.addHeader(allowHeader);
		return response;
	}

}
