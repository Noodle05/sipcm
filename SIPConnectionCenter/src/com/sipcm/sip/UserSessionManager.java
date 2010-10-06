/**
 * 
 */
package com.sipcm.sip;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("userSessionManager")
public abstract class UserSessionManager {
	private ConcurrentMap<String, UserSession> userSessions;

	protected abstract UserSession createUserSession();

	@PostConstruct
	public void init() {
		userSessions = new ConcurrentHashMap<String, UserSession>();
	}
}
