/**
 * 
 */
package com.mycallstation.web.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.mycallstation.jforumintegration.SecurityTools;

/**
 * @author wgao
 * 
 */
@Component("securityLogoutHandler")
public class SecurityLogoutHandler implements LogoutSuccessHandler {
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
		Cookie c = new Cookie(SecurityTools.FORUM_COOKIE_NAME, "");
		c.setPath("/");
		c.setMaxAge(0);
		response.addCookie(c);
		response.sendRedirect("/");
	}
}
