/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.util.EventObject;

import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public class RegistrationEventObject extends EventObject {
	private static final long serialVersionUID = -3435885228726487070L;

	public RegistrationEventObject(UserSipProfile... userSipProfiles) {
		super(userSipProfiles);
		if (userSipProfiles == null) {
			throw new NullPointerException("Cannot take null parameter.");
		}
		if (userSipProfiles.length <= 0) {
			throw new IllegalArgumentException("Cannot take empty parameter.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#getSource()
	 */
	@Override
	public UserSipProfile[] getSource() {
		return (UserSipProfile[]) source;
	}
}
