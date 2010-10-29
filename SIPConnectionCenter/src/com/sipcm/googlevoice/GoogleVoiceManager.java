/**
 * 
 */
package com.sipcm.googlevoice;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wgao
 * 
 */
public abstract class GoogleVoiceManager {
	private Logger logger = LoggerFactory.getLogger(GoogleVoiceManager.class);

	private ClientConnectionManager connMgr;

	protected abstract GoogleVoiceSession getGoogleVoiceSession();

	@PostConstruct
	public void init() {
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 50);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
				new ConnPerRouteBean(50));
		Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(),
				80);
		Scheme https = new Scheme("https", SSLSocketFactory.getSocketFactory(),
				443);
		SchemeRegistry sr = new SchemeRegistry();
		sr.register(http);
		sr.register(https);
		ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(
				params, sr);
		this.connMgr = connMgr;
	}

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

	@PreDestroy
	public void destroy() {
		connMgr.shutdown();
	}

	public ClientConnectionManager getConnectionManager() {
		return connMgr;
	}
}
