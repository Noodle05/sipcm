/**
 * 
 */
package com.sipcm.sip;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("userSessionManager")
public abstract class UserSessionManager {
	protected abstract UserSession createUserSession();
}
