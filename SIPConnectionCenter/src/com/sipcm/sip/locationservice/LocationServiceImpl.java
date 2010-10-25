/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.servlet.sip.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
public abstract class LocationServiceImpl implements LocationService {
	public static final Logger logger = LoggerFactory
			.getLogger(LocationServiceImpl.class);

	private ConcurrentMap<String, UserProfile> userProfiles;

	protected abstract UserProfile createUserProfile();

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		userProfiles = new ConcurrentHashMap<String, UserProfile>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#removeAllBinding(java.lang
	 * .String)
	 */
	@Override
	public void removeAllBinding(String key) throws UserNotFoundException {
		if (userProfiles.remove(key) == null) {
			throw new UserNotFoundException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getBinding(java.lang.String
	 * , javax.servlet.sip.Address)
	 */
	@Override
	public Binding getBinding(String key, Address address) {
		UserProfile userProfile = userProfiles.get(key);
		if (userProfile != null) {
			return userProfile.getBinding(address);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#removeBinding(java.lang
	 * .String, javax.servlet.sip.Address)
	 */
	@Override
	public void removeBinding(String key, Address address)
			throws UserNotFoundException {
		UserProfile userProfile = getUserProfile(key);
		Binding existingBinding = userProfile.getBinding(address);
		if (existingBinding != null) {
			userProfile.removeBinding(existingBinding);
		}
		if (userProfile.hasNoBinding()) {
			userProfiles.remove(key);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#updateRegistration(java
	 * .lang.String, javax.servlet.sip.Address, java.lang.String)
	 */
	@Override
	public void updateRegistration(String key, Address address, String callId)
			throws UserNotFoundException {
		UserProfile userProfile = getUserProfile(key);
		userProfile.updateBinding(address, callId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getAddresses(java.lang.
	 * String)
	 */
	@Override
	public Collection<Address> getAddresses(String key)
			throws UserNotFoundException {
		UserProfile userProfile = userProfiles.get(key);
		if (userProfile != null) {
			return userProfile.getAddresses();
		} else {
			return Collections.emptyList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#register(java.lang.String,
	 * com.sipcm.common.model.User, javax.servlet.sip.Address, java.lang.String)
	 */
	@Override
	public void register(String key, User user, Address address, String callId) {
		UserProfile userProfile = createUserProfile();
		userProfile.setAddressOfRecord(key);
		userProfile.setUser(user);
		UserProfile up = userProfiles.putIfAbsent(key, userProfile);
		if (up != null) {
			userProfile = up;
		}
		Binding binding = new Binding(address, callId);
		userProfile.addBinding(binding);
	}

	private UserProfile getUserProfile(String key) throws UserNotFoundException {
		UserProfile userProfile = userProfiles.get(key);
		if (userProfile == null) {
			throw new UserNotFoundException();
		}
		return userProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#checkContactExpires(long)
	 */
	@Override
	public void checkContactExpires() {
		Iterator<Entry<String, UserProfile>> ite = userProfiles.entrySet()
				.iterator();
		while (ite.hasNext()) {
			Entry<String, UserProfile> entry = ite.next();
			UserProfile userProfile = entry.getValue();
			if (logger.isTraceEnabled()) {
				logger.trace("Checking contacts expirestime for: {}",
						userProfile.getAddressOfRecord());
			}
			userProfile.checkContactExpires();
			if (userProfile.isEmpty()) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} has no contact remaining, remove it.",
							userProfile.getAddressOfRecord());
				}
				ite.remove();
			}
		}
	}
}
