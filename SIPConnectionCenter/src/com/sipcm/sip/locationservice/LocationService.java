package com.sipcm.sip.locationservice;

import java.net.SocketAddress;
import java.util.Collection;

import javax.servlet.sip.Address;

import com.sipcm.sip.model.UserSipProfile;

public interface LocationService {

	public void removeAllBinding(String key) throws UserNotFoundException;

	public Binding getBinding(String key, Address address);

	public void removeBinding(String key, Address address)
			throws UserNotFoundException;

	public void updateRegistration(String key, Address address,
			Address remoteEnd, SocketAddress laddr, String callId)
			throws UserNotFoundException;

	public Collection<Address> getAddresses(String key)
			throws UserNotFoundException;

	public void register(String key, UserSipProfile userSipProfile,
			Address address, Address remoteEnd, SocketAddress laddr,
			String callid);

	public void checkContactExpires();

	public UserProfile getUserProfileByKey(String key)
			throws UserNotFoundException;

	public UserProfile getUserProfileByPhoneNumber(String phoneNumber)
			throws UserNotFoundException;

	/**
	 * Get all remote end bindings for NAT keep alive ping.
	 * 
	 * @return
	 */
	public Collection<Binding> getAllRemoteEnd();

	/**
	 * Callback function when user(s) been changed.
	 * 
	 * @param userIds
	 */
	public void onUserChanged(Long... userIds);

	/**
	 * Callback function when user been disabled.
	 * 
	 * @param userIds
	 */
	public void onUserDisabled(Long... userIds);
}