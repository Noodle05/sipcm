/**
 * 
 */
package com.sipcm.sip.proxy.plugins;

import javax.sip.message.Request;
import javax.sip.message.Response;

import org.springframework.stereotype.Component;

import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@Component("optionsMethod")
public class OptionsMethod extends AbstractAuthenticatedMethod {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.proxy.plugins.AbstractAuthenticatedMethod#
	 * processAuthorizedIncomingRequest(javax.sip.message.Request,
	 * com.sipcm.common.model.User)
	 */
	@Override
	protected Response processAuthorizedIncomingRequest(Request request,
			User user) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
