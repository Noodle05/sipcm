/**
 * 
 */
package com.mycallstation.common;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

import com.mycallstation.util.CodecTool;

/**
 * @author wgao
 * 
 */
public abstract class BaseConfiguration {
	public static final String DOMAIN_NAME = "domainname";
	public static final String REALM_NAME = "sip.server.realm";
	public static final String GLOBAL_BATCH_SIZE = "global.batchsize";
	public static final String MAX_HTTP_CLIENT_TOTAL_CONNECTIONS = "com.sip.http.client.maxConnections";
	public static final String HTTP_CLIENT_GET_CONNECTION_TIMEOUT = "com.sip.http.client.getConnectionTimeout";
	public static final String GOOGLE_AUTH_APPNAME = "google.authentication.appname";

	@Resource(name = "applicationConfiguration")
	protected Configuration appConfig;

	protected final TextEncryptor textEncryptor;

	public BaseConfiguration() {
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword(CodecTool.PASSWORD);
		textEncryptor = encryptor;
	}

	public int getGlobalBatchSize() {
		return appConfig.getInt(GLOBAL_BATCH_SIZE, 500);
	}

	public String getDomain() {
		return appConfig.getString(DOMAIN_NAME);
	}

	public String getRealmName() {
		return appConfig.getString(REALM_NAME);
	}

	public int getMaxHttpClientTotalConnections() {
		return appConfig.getInt(MAX_HTTP_CLIENT_TOTAL_CONNECTIONS, 50);
	}

	public int getHttpClientConnectionTimeout() {
		return appConfig.getInt(HTTP_CLIENT_GET_CONNECTION_TIMEOUT, 5000);
	}

	public String getGoogleAuthenticationAppname() {
		return appConfig.getString(GOOGLE_AUTH_APPNAME, "MyCallStation-1.0");
	}
}
