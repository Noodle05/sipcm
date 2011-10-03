/**
 * 
 */
package com.mycallstation.googlevoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wgao
 * 
 */
public abstract class GoogleVoiceManagerImpl implements GoogleVoiceManager {
	private Logger logger = LoggerFactory
			.getLogger(GoogleVoiceManagerImpl.class);

	protected abstract GoogleVoiceSession getGoogleVoiceSession();

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
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Creating new google voice session for {}, call back number {}",
					username, myNumber);
		}
		GoogleVoiceSession session = getGoogleVoiceSession();
		session.setUsername(username);
		session.setPassword(password);
		session.setMyNumber(myNumber);
		session.init();
		return session;
	}
}
