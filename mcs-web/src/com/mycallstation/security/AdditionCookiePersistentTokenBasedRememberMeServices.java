/**
 * 
 */
package com.mycallstation.security;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import com.mycallstation.dataaccess.model.User;
import com.mycallstation.jforumintegration.SecurityTools;
import com.mycallstation.web.util.WebConfiguration;

/**
 * @author wgao
 * 
 */
public class AdditionCookiePersistentTokenBasedRememberMeServices extends
		PersistentTokenBasedRememberMeServices {
	@Resource
	private WebConfiguration appConfig;

	public AdditionCookiePersistentTokenBasedRememberMeServices()
			throws Exception {
		super();
	}

	@Override
	protected void onLoginSuccess(HttpServletRequest request,
			HttpServletResponse response,
			Authentication successfulAuthentication) {
		super.onLoginSuccess(request, response, successfulAuthentication);

		Object principal = successfulAuthentication.getPrincipal();
		if (principal != null) {
			if (principal instanceof UserDetailsImpl) {
				User user = ((UserDetailsImpl) principal).getUser();
				String encryptData = SecurityTools.getInstance()
						.encryptCookieValues(user.getEmail(),
								user.getUsername());
				Cookie c = new Cookie(SecurityTools.FORUM_COOKIE_NAME,
						encryptData);
				c.setMaxAge(getTokenValiditySeconds());
				c.setPath("/");
				c.setDomain("." + appConfig.getDomain());
				if (logger.isTraceEnabled()) {
					logger.trace("Adding cookie for jforum SSO.");
				}
				response.addCookie(c);
			}
		}
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens,
			HttpServletRequest request, HttpServletResponse response) {
		UserDetails userDetails = super.processAutoLoginCookie(cookieTokens,
				request, response);
		if (userDetails != null) {
			User user = ((UserDetailsImpl) userDetails).getUser();
			String encryptData = SecurityTools.getInstance()
					.encryptCookieValues(user.getEmail(), user.getUsername());
			Cookie c = new Cookie(SecurityTools.FORUM_COOKIE_NAME, encryptData);
			c.setMaxAge(getTokenValiditySeconds());
			c.setPath("/");
			c.setDomain("." + appConfig.getDomain());
			if (logger.isTraceEnabled()) {
				logger.trace("Adding cookie for jforum SSO.");
			}
			response.addCookie(c);
		}
		return userDetails;
	}

	@Override
	public void logout(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) {
		super.logout(request, response, authentication);
		if (logger.isDebugEnabled()) {
			logger.debug("Cancelling cookie for jforum SSO.");
		}
		Cookie cookie = new Cookie(SecurityTools.FORUM_COOKIE_NAME, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setDomain("." + appConfig.getDomain());
		response.addCookie(cookie);
	}
}
