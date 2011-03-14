/**
 * 
 */
package com.mycallstation.sip.events;

import java.util.EventObject;

import com.mycallstation.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public class RegistrationEvent extends EventObject {
	private static final long serialVersionUID = -3435885228726487070L;

	public RegistrationEvent(UserSipProfile userSipProfile) {
		super(userSipProfile);
	}

	public UserSipProfile getUserSipProfile() {
		return (UserSipProfile) source;
	}
}
