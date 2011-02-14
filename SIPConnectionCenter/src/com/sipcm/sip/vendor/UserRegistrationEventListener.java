/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.Collection;

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
			Collection<UserVoipAccount> incomingAccounts = userVoipAccountService
					.getIncomingAccounts(userSipProfile);
			if (incomingAccounts != null && !incomingAccounts.isEmpty()) {
				voipVendorManager.registerForIncomingRequest(userSipProfile,
						incomingAccounts);
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
			Collection<UserVoipAccount> incomingAccounts = userVoipAccountService
					.getIncomingAccounts(userSipProfile);
			if (incomingAccounts != null && !incomingAccounts.isEmpty()) {
				voipVendorManager.unregisterForIncomingRequest(userSipProfile,
						incomingAccounts);
			}
		}
	}
}
