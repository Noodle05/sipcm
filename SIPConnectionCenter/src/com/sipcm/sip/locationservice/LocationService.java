package com.sipcm.sip.locationservice;

import java.util.Collection;

import javax.servlet.sip.Address;

import com.sipcm.sip.model.UserSipBinding;
import com.sipcm.sip.model.UserSipProfile;

public interface LocationService {

	public void removeAllBinding(String key);

	public void updateRegistration(String key, UserSipProfile userSipProfile,
			Address address, Address remoteEnd, String callId);

	public Collection<Address> getAddresses(String key);

	public void checkContactExpires();

	public UserSipBinding getUserSipBindingByKey(String key);

	public UserSipBinding getUserSipBindingByPhoneNumber(String phoneNumber);

	/**
	 * Callback function when user(s) been changed.
	 * 
	 * @param userIds
	 */
	public void onUserChanged(Long... userIds);
}