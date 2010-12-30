/**
 * 
 */
package com.sipcm.sip;

import java.util.EventObject;

import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public class UserSipProfileEventObject extends EventObject {
	private static final long serialVersionUID = -779543110255062876L;

	public UserSipProfileEventObject(UserSipProfile... userSipProfiles) {
		super(userSipProfiles);
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
