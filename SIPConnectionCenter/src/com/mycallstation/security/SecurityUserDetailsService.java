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
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.common.business.UserService;
import com.mycallstation.common.model.User;

/**
 * @author wgao
 * 
 */
@Component("securityUserDetailsService")
@Transactional(readOnly = true)
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
		User user = userService.getUserByUsername(username);
		if (user != null) {
			return new UserDetailsImpl(user);
		} else {
			throw new UsernameNotFoundException(
					"Cannot found user based on username: " + username);
		}
	}
}
