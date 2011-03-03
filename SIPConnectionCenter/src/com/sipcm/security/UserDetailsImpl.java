/**
 * 
 */
package com.sipcm.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sipcm.common.model.Role;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 4541159727550255614L;
	private final User user;
	private final Collection<GrantedAuthority> authorities;

	public UserDetailsImpl(User user) {
		this.user = user;
		authorities = new ArrayList<GrantedAuthority>(user.getRoles().size());
		for (Role role : user.getRoles()) {
			if (role.getDeleteDate() == null) {
				authorities.add(GrantedAuthorityImpl.getGrantedAuthority(role
						.getName()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getAuthorities
	 * ()
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getPassword()
	 */
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getUsername()
	 */
	@Override
	public String getUsername() {
		return user.getUsername();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired
	 * ()
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked
	 * ()
	 */
	@Override
	public boolean isAccountNonLocked() {
		return user.getDeleteDate() == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isCredentialsNonExpired()
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return user.getStatus().isActive();
	}

	public User getUser() {
		return user;
	}
}
