/**
 * 
 */
package com.sipcm.sip;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@Component("userSession")
@Scope("prototype")
public class UserSession {
	private User user;

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
}
