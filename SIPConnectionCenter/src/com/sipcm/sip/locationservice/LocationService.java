package com.sipcm.sip.locationservice;

import java.util.Collection;

import javax.servlet.sip.Address;

import com.sipcm.common.model.User;

public interface LocationService {

	public void removeAllBinding(String key) throws UserNotFoundException;

	public Binding getBinding(String key, Address address);

	public void removeBinding(String key, Address address)
			throws UserNotFoundException;

	public void updateRegistration(String key, Address address, String callId)
			throws UserNotFoundException;

	public Collection<Address> getAddresses(String key)
			throws UserNotFoundException;

	public void register(String key, User user, Address address, String callid);

	public void checkContactExpires();
}