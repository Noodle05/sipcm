/**
 * 
 */
package com.mycallstation.sip.util;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.mobicents.servlet.sip.startup.loading.SipLoginConfig;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sipLoginConfig")
public class LoginConfig extends SipLoginConfig {
	private static final long serialVersionUID = 4540900505386597721L;

	@Resource(name = "systemConfiguration")
	private SipConfiguration appConfig;

	@PostConstruct
	public void init() {
		this.setAuthMethod(DIGEST_AUTHENTICATION_METHOD);
		this.setRealmName(appConfig.getRealmName());
	}
}
