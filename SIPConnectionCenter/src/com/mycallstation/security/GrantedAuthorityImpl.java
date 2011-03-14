/**
 * 
 */
package com.mycallstation.security;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.MapMaker;

/**
 * @author wgao
 * 
 */
public class GrantedAuthorityImpl implements GrantedAuthority {
	private static final long serialVersionUID = -395218563364427760L;

	private static final ConcurrentMap<String, GrantedAuthority> cache = new MapMaker()
			.concurrencyLevel(2).initialCapacity(2)
			.expireAfterWrite(8, TimeUnit.HOURS).makeMap();

	private final String authority;

	private GrantedAuthorityImpl(String authority) {
		this.authority = authority;
	}

	public static GrantedAuthority getGrantedAuthority(String authority) {
		GrantedAuthority auth = cache.get(authority);
		if (auth == null) {
			auth = new GrantedAuthorityImpl(authority);
			GrantedAuthority tmp = cache.putIfAbsent(authority, auth);
			if (tmp != null) {
				auth = tmp;
			}
		}
		return auth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.GrantedAuthority#getAuthority()
	 */
	@Override
	public String getAuthority() {
		return authority;
	}
}
