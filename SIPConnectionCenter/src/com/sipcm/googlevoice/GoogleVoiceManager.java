/**
 * 
 */
package com.sipcm.googlevoice;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("googleVoiceManager")
public class GoogleVoiceManager {
	private ClientConnectionManager connMgr;

	@PostConstruct
	public void init() {
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
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

	@PreDestroy
	public void destroy() {
		connMgr.shutdown();
	}

	public ClientConnectionManager getConnectionManager() {
		return connMgr;
	}
}
