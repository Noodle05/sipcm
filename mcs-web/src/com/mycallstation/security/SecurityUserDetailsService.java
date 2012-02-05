/**
 * 
 */
package com.mycallstation.security;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.business.UserService;
import com.mycallstation.dataaccess.model.User;

/**
 * @author Wei Gao
 * 
 */
@Component("securityUserDetailsService")
public class SecurityUserDetailsService implements UserDetailsService {
	@Resource(name = "userService")
	private UserService userService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#
	 * loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		User user = userService.fullyLoadUserByUsername(username);
		if (user != null) {
			return new UserDetailsImpl(user);
		} else {
			throw new UsernameNotFoundException(
					"Cannot found user based on username: " + username);
		}
	}
}
