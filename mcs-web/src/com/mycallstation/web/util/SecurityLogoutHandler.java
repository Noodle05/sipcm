/**
 * 
 */
package com.mycallstation.web.util;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.mycallstation.jforumintegration.SecurityTools;

/**
 * @author Wei Gao
 * 
 */
@Component("securityLogoutHandler")
public class SecurityLogoutHandler implements LogoutSuccessHandler {
	@Resource(name = "systemConfiguration")
	private WebConfiguration appConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.web.authentication.logout.LogoutSuccessHandler
	 * #onLogoutSuccess(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.Authentication)
	 */
	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if (c.getName().equals(SecurityTools.FORUM_COOKIE_NAME)) {
					c.setMaxAge(0);
					c.setDomain("." + appConfig.getDomain());
					response.addCookie(c);
				}
			}
		}
		response.sendRedirect("/");
	}
}
