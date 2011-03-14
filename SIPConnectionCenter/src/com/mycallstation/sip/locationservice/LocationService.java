package com.mycallstation.sip.locationservice;

import java.util.Collection;

import javax.servlet.sip.Address;

import com.mycallstation.sip.model.AddressBinding;
import com.mycallstation.sip.model.UserSipProfile;

public interface LocationService {

	public void removeAllBinding(UserSipProfile userSipProfile);

	public void updateRegistration(UserSipProfile userSipProfile,
			Address address, int expires, Address remoteEnd, String callId);

	public Collection<Address> getAddresses(UserSipProfile userSipProfile);

	public void checkContactExpires();

	// public UserSipBinding getUserSipBindingByKey(String key);

	public Collection<AddressBinding> getUserSipBindingByPhoneNumber(
			String phoneNumber);

	public Collection<AddressBinding> getUserBinding(
			UserSipProfile userSipProfile);

	/**
	 * Callback function when user(s) been changed.
	 * 
	 * @param userIds
	 */
	public void onUserChanged(Long... userIds);
}
