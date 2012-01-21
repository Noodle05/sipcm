/**
 * 
 */
package com.mycallstation.googlevoice;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author wgao
 * 
 */
public abstract class GoogleVoiceManagerImpl implements GoogleVoiceManager {
	private Logger logger = LoggerFactory
			.getLogger(GoogleVoiceManagerImpl.class);

	private final LoadingCache<String, GoogleVoiceSession> cache;

	public GoogleVoiceManagerImpl() {
		cache = CacheBuilder.newBuilder().concurrencyLevel(16).softValues()
				.expireAfterAccess(10L, TimeUnit.MINUTES).maximumSize(200)
				.build(new CacheLoader<String, GoogleVoiceSession>() {
					@Override
					public GoogleVoiceSession load(String key) throws Exception {
						if (logger.isTraceEnabled()) {
							logger.trace(
									"Google voice session for user {} not in cache, create it.",
									key);
						}
						GoogleVoiceSession gvSession = createGoogleVoiceSession();
						gvSession.setUsername(key);
						return gvSession;
					}
				});
	}

	protected abstract GoogleVoiceSession createGoogleVoiceSession();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.googlevoice.GoogleVoiceManager#getGoogleVoiceSession
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public GoogleVoiceSession getGoogleVoiceSession(String username,
			String password) {
		return getGoogleVoiceSession(username, password, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.googlevoice.GoogleVoiceManager#getGoogleVoiceSession
	 * (java.lang .String, java.lang.String, java.lang.String)
	 */
	@Override
	public GoogleVoiceSession getGoogleVoiceSession(String username,
			String password, String myNumber) {
		if (username == null) {
			throw new NullPointerException("Account cannot be null.");
		}
		if (password == null) {
			throw new NullPointerException("Password cannot be null.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Creating new google voice session for {}, call back number {}",
					username, myNumber);
		}
		GoogleVoiceSession session = cache.getUnchecked(username);
		session.setPassword(password);
		if (myNumber != null) {
			session.setMyNumber(myNumber);
		}
		return session;
	}
}
