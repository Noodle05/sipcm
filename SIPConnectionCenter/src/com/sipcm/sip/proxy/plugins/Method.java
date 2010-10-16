/**
 * 
 */
package com.sipcm.sip.proxy.plugins;

import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * @author wgao
 * 
 */
public interface Method {
	public Response processRequest(Request request) throws Exception;
}
