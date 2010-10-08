/**
 * 
 */
package com.sipcm.sip;

import gov.nist.javax.sip.clientauthutils.DigestServerAuthenticationHelper;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sip.header.ProxyAuthorizationHeader;
import javax.sip.message.Request;

import org.apache.commons.configuration.Configuration;
import org.springframework.stereotype.Component;

import com.sipcm.common.AuthenticationException;
import com.sipcm.common.InvalidPasswordException;
import com.sipcm.common.NoSuchUserException;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@Component("userSessionManager")
public abstract class UserSessionManager {
	public static final String SIP_REALM = "sipcm.realm";
	public static final String DEFAULT_REALM = "sipcm.com";

	private ConcurrentMap<String, UserSession> userSessions;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "applicationConfiguration")
	private Configuration appConf;

	private DigestServerAuthenticationHelper dsah;

	protected abstract UserSession createUserSession();

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		userSessions = new ConcurrentHashMap<String, UserSession>();
		dsah = new DigestServerAuthenticationHelper();
	}

	private String getSipRealm() {
		return appConf.getString(SIP_REALM, DEFAULT_REALM);
	}

	public UserSession register(Request request) throws AuthenticationException {
		ProxyAuthorizationHeader authHeader = (ProxyAuthorizationHeader) request
				.getHeader(ProxyAuthorizationHeader.NAME);
		if (authHeader == null) {
			throw new NoAuthHeaderException();
		}
		String sipId = authHeader.getUsername();

		if (sipId == null) {
			throw new NoAuthHeaderException();
		}
		User user = userService.getUserBySipId(sipId);
		if (user == null) {
			throw new NoSuchUserException(sipId);
		}
		if (!dsah.doAuthenticatePlainTextPassword(request,
				user.getSipPassword())) {
			throw new InvalidPasswordException();
		}

		UserSession userSession = createUserSession();
		userSession.setUser(user);
		return userSession;
	}
}
