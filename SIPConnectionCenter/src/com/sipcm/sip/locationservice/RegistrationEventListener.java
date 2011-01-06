/**
 * 
 */
package com.sipcm.sip.locationservice;


/**
 * @author wgao
 * 
 */
public interface RegistrationEventListener {
	public void userRegistered(RegistrationEventObject event);

	public void userUnregistered(RegistrationEventObject event);
}
