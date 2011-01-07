/**
 * 
 */
package com.sipcm.sip.events;



/**
 * @author wgao
 * 
 */
public interface RegistrationEventListener {
	public void userRegistered(RegistrationEventObject event);

	public void userUnregistered(RegistrationEventObject event);
}
