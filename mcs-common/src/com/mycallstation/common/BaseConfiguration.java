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
	public static final String MAX_HTTP_CLIENT_TOTAL_CONNECTIONS = "com.sip.http.client.maxConnections";
	public static final String HTTP_CLIENT_GET_CONNECTION_TIMEOUT = "com.sip.http.client.getConnectionTimeout";

	@Resource(name = "applicationConfiguration")
	protected Configuration appConfig;

	protected final TextEncryptor textEncryptor;

	public BaseConfiguration() {
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword(CodecTool.PASSWORD);
		textEncryptor = encryptor;
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
}
