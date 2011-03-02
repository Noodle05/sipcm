/**
 * 
 */
package com.sipcm.util;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.mobicents.servlet.sip.startup.loading.SipLoginConfig;
import org.springframework.stereotype.Component;

import com.sipcm.common.SystemConfiguration;

/**
 * @author wgao
 * 
 */
@Component("sipLoginConfig")
public class LoginConfig extends SipLoginConfig {
	private static final long serialVersionUID = 4540900505386597721L;

	@Resource(name = "systemConfiguration")
	private SystemConfiguration appConfig;

	@PostConstruct
	public void init() {
		this.setAuthMethod(DIGEST_AUTHENTICATION_METHOD);
		this.setRealmName(appConfig.getRealmName());
	}
}
