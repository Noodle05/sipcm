/**
 * 
 */
package com.sipcm.sip.events;

import java.util.EventListener;

/**
 * @author wgao
 * 
 */
public interface RegistrationEventListener extends EventListener {
	public void userRegistered(RegistrationEvent event);

	public void userUnregistered(RegistrationEvent event);
}
