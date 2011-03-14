/**
 * 
 */
package com.mycallstation.security;

import javax.annotation.Resource;

import org.jasypt.digest.StringDigester;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mycallstation.common.SystemConfiguration;

/**
 * @author wgao
 * 
 */
@Component("securityAuthenticationProvider")
public class SecurityAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {
	@Resource(name = "systemConfiguration")
	private SystemConfiguration appConfig;

	private String realm;

	// ~ Instance fields
	// ================================================================================================

	@Resource(name = "globalStringDigester")
	private StringDigester stringDigster;

	@Resource(name = "securityUserDetailsService")
	private UserDetailsService userDetailsService;

	// ~ Methods
	// ========================================================================================================
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), userDetails);
		}

		String presentedPassword = authentication.getCredentials().toString();

		if (!stringDigster.matches(userDetails.getUsername() + ":" + realm
				+ ":" + presentedPassword, userDetails.getPassword())) {
			logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), userDetails);
		}
	}

	@Override
	protected void doAfterPropertiesSet() throws Exception {
		super.doAfterPropertiesSet();
		Assert.notNull(this.userDetailsService,
				"A UserDetailsService must be set");
		Assert.notNull(this.appConfig, "Application configuration must be set");
		realm = appConfig.getRealmName();
	}

	@Override
	protected final UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;

		try {
			loadedUser = userDetailsService.loadUserByUsername(username);
		} catch (DataAccessException repositoryProblem) {
			throw new AuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new AuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}
		return loadedUser;
	}
}
