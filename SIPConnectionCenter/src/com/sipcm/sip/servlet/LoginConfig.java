/**
 * 
 */
package com.sipcm.sip.servlet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.mobicents.servlet.sip.startup.loading.SipLoginConfig;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sipLoginConfig")
public class LoginConfig extends SipLoginConfig {
	private static final long serialVersionUID = 4540900505386597721L;

	public static final String REALM_NAME = "sip.server.realm";

	@Resource(name = "applicationConfiguration")
	private Configuration appConfig;

	@PostConstruct
	public void init() {
		this.setAuthMethod(DIGEST_AUTHENTICATION_METHOD);
		this.setRealmName(appConfig.getString(REALM_NAME));
	}
}
