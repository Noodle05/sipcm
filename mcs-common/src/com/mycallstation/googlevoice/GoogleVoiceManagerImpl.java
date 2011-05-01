/**
 * 
 */
package com.mycallstation.googlevoice;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.mycallstation.common.BaseConfiguration;

/**
 * @author wgao
 * 
 */
public abstract class GoogleVoiceManagerImpl implements GoogleVoiceManager {
	private Logger logger = LoggerFactory
			.getLogger(GoogleVoiceManagerImpl.class);

	@Resource(name = "systemConfiguration")
	private BaseConfiguration appConfig;

	private ThreadSafeClientConnManager connMgr;

	protected abstract GoogleVoiceSession getGoogleVoiceSession();

	@PostConstruct
	public void init() {
		int maxConnections = appConfig.getMaxHttpClientTotalConnections();
		ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager();
		connMgr.setMaxTotal(maxConnections);
		connMgr.setDefaultMaxPerRoute(maxConnections);
		this.connMgr = connMgr;
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

	@Scheduled(fixedRate = 60000L)
	public void checkHttpConnection() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Closing expired and idle connections.");
			}
			if (logger.isTraceEnabled()) {
				logger.trace("{} connections in pool before close.",
						connMgr.getConnectionsInPool());
			}
			connMgr.closeExpiredConnections();
			connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
			if (logger.isTraceEnabled()) {
				logger.trace("{} connections in pool after close.",
						connMgr.getConnectionsInPool());
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error happened when close expired and idle connection");
				if (logger.isDebugEnabled()) {
					logger.debug("Exception stack: ", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.googlevoice.GoogleVoiceManager#getConnectionManager()
	 */
	@Override
	public ClientConnectionManager getConnectionManager() {
		return connMgr;
	}
}
