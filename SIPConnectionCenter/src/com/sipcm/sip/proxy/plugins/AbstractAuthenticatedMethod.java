/**
 * 
 */
package com.sipcm.sip.proxy.plugins;

import gov.nist.javax.sip.clientauthutils.DigestServerAuthenticationHelper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sip.header.ProxyAuthorizationHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.springframework.context.annotation.DependsOn;

import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@DependsOn({ "sipCenter" })
public abstract class AbstractAuthenticatedMethod extends AbstractMethod {
	protected DigestServerAuthenticationHelper digestServerAuthenticationHelper;

	public static final String SIP_REALM = "sip.server.realm";

	@Resource(name = "userService")
	private UserService userService;

	@PostConstruct
	public void init() throws Exception {
		digestServerAuthenticationHelper = new DigestServerAuthenticationHelper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.proxy.plugins.AbstractMethod#processRequest(javax.sip.message
	 * .Request)
	 */
	@Override
	protected Response processIncomingRequest(Request request) throws Exception {
		boolean authorized = false;
		User user = null;
		// Authentication first.
		ProxyAuthorizationHeader authHeader = (ProxyAuthorizationHeader) request
				.getHeader(ProxyAuthorizationHeader.NAME);
		if (authHeader != null) {
			String username = authHeader.getUsername();
			user = userService.getUserBySipId(username);
			if (user == null) {
				return messageFactory.createResponse(Response.NOT_FOUND,
						request);
			} else {
				if (digestServerAuthenticationHelper
						.doAuthenticatePlainTextPassword(request,
								user.getSipPassword())) {
					authorized = true;
				}
			}
		}
		if (!authorized) {
			Response response = messageFactory.createResponse(
					Response.PROXY_AUTHENTICATION_REQUIRED, request);
			digestServerAuthenticationHelper.generateChallenge(headerFactory,
					response, getRealm());
			return response;
		}
		assert user != null;
		return processAuthorizedIncomingRequest(request, user);
	}

	protected abstract Response processAuthorizedIncomingRequest(
			Request request, User user) throws Exception;

	private String getRealm() {
		return config.getString(SIP_REALM);
	}
}
