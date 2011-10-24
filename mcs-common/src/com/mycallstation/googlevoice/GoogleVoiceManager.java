package com.mycallstation.googlevoice;

public interface GoogleVoiceManager {
	public GoogleVoiceSession getGoogleVoiceSession(String username,
			String password);

	public GoogleVoiceSession getGoogleVoiceSession(String username,
			String password, String myNumber);
}
