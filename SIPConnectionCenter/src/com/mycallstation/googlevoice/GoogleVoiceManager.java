package com.mycallstation.googlevoice;

import org.apache.http.conn.ClientConnectionManager;

public interface GoogleVoiceManager {

	public GoogleVoiceSession getGoogleVoiceSession(String username,
			String password, String myNumber);

	public ClientConnectionManager getConnectionManager();

}
