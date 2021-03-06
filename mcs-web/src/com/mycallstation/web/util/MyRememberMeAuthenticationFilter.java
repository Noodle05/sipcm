/**
 * 
 */
package com.mycallstation.web.util;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.mycallstation.dataaccess.model.User;
import com.mycallstation.jforumintegration.SecurityTools;
import com.mycallstation.security.UserDetailsImpl;

/**
 * @author Wei Gao
 * 
 */
@Component("myRememberMeAuthenticationFilter")
public class MyRememberMeAuthenticationFilter extends GenericFilterBean {
	@Resource(name = "systemConfiguration")
	public WebConfiguration appConfig;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if (req != null
				&& req instanceof HttpServletRequest
				&& res != null
				&& res instanceof HttpServletResponse
				&& SecurityContextHolder.getContext() != null
				&& SecurityContextHolder.getContext().getAuthentication() != null) {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			boolean foundCookie = false;
			if (request.getCookies() != null) {
				for (Cookie c : request.getCookies()) {
					if (SecurityTools.FORUM_COOKIE_NAME.equals(c.getName())) {
						foundCookie = true;
						break;
					}
				}
			}
			if (!foundCookie) {
				Authentication auth = SecurityContextHolder.getContext()
						.getAuthentication();
				Object principal = auth.getPrincipal();
				if (principal != null) {
					if (principal instanceof UserDetailsImpl) {
						User user = ((UserDetailsImpl) principal).getUser();
						String encryptData = SecurityTools.getInstance()
								.encryptCookieValues(user.getEmail(),
										user.getUsername());
						Cookie c = new Cookie(SecurityTools.FORUM_COOKIE_NAME,
								encryptData);
						c.setMaxAge(-1);
						c.setPath("/");
						c.setDomain(appConfig.getDomain());
						if (logger.isTraceEnabled()) {
							logger.trace("Adding cookie for jforum SSO. domain: "
									+ c.getDomain());
						}
						response.addCookie(c);
					}
				}
			}
		}
		chain.doFilter(req, res);
	}
}
