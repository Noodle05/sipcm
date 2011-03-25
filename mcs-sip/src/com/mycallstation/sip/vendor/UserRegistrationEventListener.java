/**
 * 
 */
package com.mycallstation.sip.vendor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.sip.events.RegistrationEvent;
import com.mycallstation.sip.events.RegistrationEventListener;

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
	 * com.mycallstation.sip.events.RegistrationEventListener#userRegistered
	 * (com.mycallstation .sip.events.RegistrationEventObject)
	 */
	@Override
	public void userRegistered(RegistrationEvent event) {
		UserSipProfile userSipProfile = event.getUserSipProfile();
		if (logger.isInfoEnabled()) {
			logger.info("User: \"{}\" logged in.", userSipProfile);
		}
		voipVendorManager.registerForIncomingRequest(userSipProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.RegistrationEventListener#userUnregistered
	 * (com.mycallstation .sip.events.RegistrationEventObject)
	 */
	@Override
	public void userUnregistered(RegistrationEvent event) {
		UserSipProfile userSipProfile = event.getUserSipProfile();
		if (logger.isInfoEnabled()) {
			logger.info("User: \"{}\" logged out.", userSipProfile);
		}
		voipVendorManager.unregisterForIncomingRequest(userSipProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.events.RegistrationEventListener#userRenewRegistration
	 * ( com.mycallstation.sip.events.RegistrationEvent)
	 */
	@Override
	public void userRenewRegistration(RegistrationEvent event) {
		UserSipProfile userSipProfile = event.getUserSipProfile();
		if (logger.isDebugEnabled()) {
			logger.debug("User: \"{}\" renewed registration.", userSipProfile);
		}
		voipVendorManager.renewForIncomingRequest(userSipProfile);
	}
}
