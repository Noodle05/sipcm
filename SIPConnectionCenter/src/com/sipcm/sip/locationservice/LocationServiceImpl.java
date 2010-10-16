/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;

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

	private ConcurrentMap<SipURI, UserProfile> userProfiles;

	protected abstract UserProfile createUserProfile();

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		userProfiles = new ConcurrentHashMap<SipURI, UserProfile>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#removeAllBinding(javax.
	 * sip.address.SipURI)
	 */
	@Override
	public void removeAllBinding(SipURI key) throws UserNotFoundException {
		if (userProfiles.remove(key) == null) {
			throw new UserNotFoundException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getBinding(javax.sip.address
	 * .SipURI, javax.sip.header.ContactHeader)
	 */
	@Override
	public Binding getBinding(SipURI key, ContactHeader contactHeader) {
		UserProfile userProfile = userProfiles.get(key);
		if (userProfile != null) {
			return userProfile.getBinding(contactHeader);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#removeBinding(javax.sip
	 * .address.SipURI, javax.sip.header.ContactHeader)
	 */
	@Override
	public void removeBinding(SipURI key, ContactHeader contactHeader)
			throws UserNotFoundException {
		UserProfile userProfile = getUserProfile(key);
		Binding existingBinding = userProfile.getBinding(contactHeader);
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
	 * com.sipcm.sip.locationservice.LocationService#updateRegistration(javax
	 * .sip.address.SipURI, javax.sip.header.ContactHeader, java.lang.String,
	 * long)
	 */
	@Override
	public void updateRegistration(SipURI key, ContactHeader contactHeader,
			String callId, long cseq) throws UserNotFoundException {
		UserProfile userProfile = getUserProfile(key);
		userProfile.updateBinding(contactHeader, callId, cseq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getContactHeaders(javax
	 * .sip.address.SipURI)
	 */
	@Override
	public Collection<ContactHeader> getContactHeaders(SipURI key)
			throws UserNotFoundException {
		UserProfile userProfile = userProfiles.get(key);
		if (userProfile != null) {
			return userProfile.getContactHeaders();
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void register(SipURI key, User user, ContactHeader contactHeader,
			String callId, long cseq) {
		UserProfile userProfile = createUserProfile();
		userProfile.setAddressOfRecord(key);
		userProfile.setUser(user);
		UserProfile up = userProfiles.putIfAbsent(key, userProfile);
		if (up != null) {
			userProfile = up;
		}
		Binding binding = new Binding(contactHeader, callId, cseq);
		userProfile.addBinding(binding);
	}

	public UserProfile getUserProfile(SipURI key) throws UserNotFoundException {
		UserProfile userProfile = userProfiles.get(key);
		if (userProfile == null) {
			throw new UserNotFoundException();
		}
		return userProfile;
	}
}
