/**
 * 
 */
package com.sipcm.sip.vendor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sipcm.sip.events.RegistrationEventListener;
import com.sipcm.sip.events.RegistrationEventObject;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Component("sipUserRegistrationForIncomingListener")
public class UserRegistrationEventListener implements RegistrationEventListener {
	private static final Logger logger = LoggerFactory
			.getLogger(UserRegistrationEventListener.class);

	@Resource(name = "voipVendorManager")
	private VoipVendorManager voipVendorManager;

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
			if (logger.isInfoEnabled()) {
				logger.info("User: \"{}\" logged in.", userSipProfile);
			}
			voipVendorManager.registerForIncomingRequest(userSipProfile);
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
			if (logger.isInfoEnabled()) {
				logger.info("User: \"{}\" logged out.", userSipProfile);
			}
			voipVendorManager.unregisterForIncomingRequest(userSipProfile);
		}
	}
}
