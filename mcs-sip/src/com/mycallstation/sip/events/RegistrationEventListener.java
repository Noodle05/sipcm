/**
 * 
 */
package com.mycallstation.sip.events;

import java.util.EventListener;

/**
 * @author Wei Gao
 * 
 */
public interface RegistrationEventListener extends EventListener {
	public void userRegistered(RegistrationEvent event);

	public void userUnregistered(RegistrationEvent event);

	public void userRenewRegistration(RegistrationEvent event);
}
