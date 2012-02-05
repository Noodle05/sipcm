/**
 * 
 */
package com.mycallstation.security;

import java.util.concurrent.TimeUnit;

import org.springframework.security.core.GrantedAuthority;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Wei Gao
 * 
 */
public class GrantedAuthorityImpl implements GrantedAuthority {
	private static final long serialVersionUID = -395218563364427760L;

	private static final LoadingCache<String, GrantedAuthority> cache = CacheBuilder
			.newBuilder().concurrencyLevel(2).initialCapacity(2)
			.expireAfterWrite(8, TimeUnit.HOURS)
			.build(new CacheLoader<String, GrantedAuthority>() {
				@Override
				public GrantedAuthority load(String key) throws Exception {
					return new GrantedAuthorityImpl(key);
				}
			});

	private final String authority;

	private GrantedAuthorityImpl(String authority) {
		this.authority = authority;
	}

	public static GrantedAuthority getGrantedAuthority(String authority) {
		GrantedAuthority auth = cache.getUnchecked(authority);
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
