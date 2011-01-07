/**
 * 
 */
package com.sipcm.sip.events;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.common.OnlineStatus;
import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Component("userSipProfileOnlineStatusUpdater")
public class UserSipProfileOnlineStatusUpdater implements
		RegistrationEventListener {
	@Resource(name = "userSipProfileService")
	public UserSipProfileService userSipProfileService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.RegistrationEventListener#userRegistered
	 * (com.sipcm.sip.locationservice.RegistrationEventObject)
	 */
	@Override
	public void userRegistered(RegistrationEventObject event) {
		UserSipProfile[] userSipProfiles = event.getSource();
		userSipProfileService.updateOnlineStatus(OnlineStatus.ONLINE,
				userSipProfiles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.RegistrationEventListener#userUnregistered
	 * (com.sipcm.sip.locationservice.RegistrationEventObject)
	 */
	@Override
	public void userUnregistered(RegistrationEventObject event) {
		UserSipProfile[] userSipProfiles = event.getSource();
		userSipProfileService.updateOnlineStatus(OnlineStatus.OFFLINE,
				userSipProfiles);
	}
}
