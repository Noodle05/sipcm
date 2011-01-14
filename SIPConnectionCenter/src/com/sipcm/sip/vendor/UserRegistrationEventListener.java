/**
 * 
 */
package com.sipcm.sip.vendor;

import javax.annotation.Resource;

import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.events.RegistrationEventListener;
import com.sipcm.sip.events.RegistrationEventObject;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public class UserRegistrationEventListener implements RegistrationEventListener {
	@Resource(name = "voipVendorManager")
	private VoipVendorManager voipVendorManager;

	@Resource(name = "userVoidAccountService")
	private UserVoipAccountService userVoipAccountService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.RegistrationEventListener#userRegistered(com.sipcm
	 * .sip.events.RegistrationEventObject)
	 */
	@Override
	public void userRegistered(RegistrationEventObject event) {
		UserSipProfile[] userSipProfiles = event.getSource();
		for (UserSipProfile userSipProfile : userSipProfiles) {
			UserVoipAccount incomingAccount = userVoipAccountService
					.getIncomingAccount(userSipProfile);
			if (incomingAccount == null) {
				voipVendorManager.registerForIncomingRequest(userSipProfile,
						incomingAccount);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.RegistrationEventListener#userUnregistered(com.sipcm
	 * .sip.events.RegistrationEventObject)
	 */
	@Override
	public void userUnregistered(RegistrationEventObject event) {
		UserSipProfile[] userSipProfiles = event.getSource();
		for (UserSipProfile userSipProfile : userSipProfiles) {
			UserVoipAccount incomingAccount = userVoipAccountService
					.getIncomingAccount(userSipProfile);
			if (incomingAccount == null) {
				voipVendorManager.unregisterForIncomingRequest(userSipProfile,
						incomingAccount);
			}
		}
	}
}
