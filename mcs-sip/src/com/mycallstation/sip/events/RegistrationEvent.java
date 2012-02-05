/**
 * 
 */
package com.mycallstation.sip.events;

import java.util.EventObject;

import com.mycallstation.dataaccess.model.UserSipProfile;

/**
 * @author Wei Gao
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
