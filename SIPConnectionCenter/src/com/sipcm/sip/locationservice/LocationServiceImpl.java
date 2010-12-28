/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.net.SocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.sip.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
public abstract class LocationServiceImpl implements LocationService {
	public static final Logger logger = LoggerFactory
			.getLogger(LocationServiceImpl.class);

	private ConcurrentMap<String, UserProfile> userProfiles;

	protected abstract UserProfile createUserProfile();

	@Resource(name = "phoneNumberUtil")
	private PhoneNumberUtil phoneNumberUtil;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

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
		UserProfile userProfile = getUserProfileByKey(key);
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
	public void updateRegistration(String key, Address address,
			Address remoteEnd, SocketAddress laddr, String callId)
			throws UserNotFoundException {
		UserProfile userProfile = getUserProfileByKey(key);
		userProfile.updateBinding(address, remoteEnd, laddr, callId);
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
	 * com.sipcm.sip.model.UserSipProfile, javax.servlet.sip.Address,
	 * java.lang.String)
	 */
	@Override
	public void register(String key, UserSipProfile userSipProfile,
			Address address, Address remoteEnd, SocketAddress laddr,
			String callId) {
		UserProfile userProfile = createUserProfile();
		userProfile.setAddressOfRecord(key);
		userProfile.setUserSipProfile(userSipProfile);
		UserProfile up = userProfiles.putIfAbsent(key, userProfile);
		if (up != null) {
			userProfile = up;
		}
		Binding binding = new Binding(address, remoteEnd, laddr, callId);
		userProfile.addBinding(binding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getUserProfileByKey(java
	 * .lang .String)
	 */
	@Override
	public UserProfile getUserProfileByKey(String key)
			throws UserNotFoundException {
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
	 * com.sipcm.sip.locationservice.LocationService#getUserProfileByPhoneNumber
	 * (java.lang.String)
	 */
	@Override
	public UserProfile getUserProfileByPhoneNumber(String phoneNumber)
			throws UserNotFoundException {
		if (phoneNumber == null)
			throw new NullPointerException("Phone number cannot be null.");
		String pn = phoneNumberUtil.getCanonicalizedPhoneNumber(phoneNumber);
		for (UserProfile profile : userProfiles.values()) {
			String p = profile.getUserSipProfile().getPhoneNumber() == null ? null
					: phoneNumberUtil.getCanonicalizedPhoneNumber(profile
							.getUserSipProfile().getPhoneNumber());
			if (pn.equals(p)) {
				if (profile.getUserSipProfile().isAllowLocalDirectly()) {
					return profile;
				}
				break;
			}
		}
		throw new UserNotFoundException();
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
				if (logger.isInfoEnabled()) {
					logger.info("{} has no contact remaining, remove it.",
							entry.getKey());
				}
				ite.remove();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.locationservice.LocationService#getAllRemoteEnd()
	 */
	@Override
	public Collection<Binding> getAllRemoteEnd() {
		Collection<Binding> remoteEnds = new ArrayList<Binding>(
				userProfiles.size());
		for (UserProfile userProfile : userProfiles.values()) {
			remoteEnds.addAll(userProfile.getBindings());
		}
		return remoteEnds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#onUserChanged(java.lang
	 * .Long[])
	 */
	@Override
	public void onUserChanged(Long... userIds) {
		if (logger.isDebugEnabled()) {
			logger.debug("User changed! ids: " + Arrays.toString(userIds));
		}
		List<Long> ids = Arrays.asList(userIds);
		Collections.sort(ids);
		Iterator<Entry<String, UserProfile>> ite = userProfiles.entrySet()
				.iterator();
		while (ite.hasNext() && !ids.isEmpty()) {
			Entry<String, UserProfile> entry = ite.next();
			UserProfile userProfile = entry.getValue();
			int index = Collections.binarySearch(ids, userProfile
					.getUserSipProfile().getId());
			if (index >= 0) {
				ids.remove(index);
				UserSipProfile userSipProfile = userSipProfileService
						.getEntityById(userProfile.getUserSipProfile().getId());
				if (userSipProfile == null
						|| !userSipProfile.getOwner().getStatus().isActive()) {
					ite.remove();
				} else {
					userProfile.setUserSipProfile(userSipProfile);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#onUserDisabled(java.lang
	 * .Long[])
	 */
	@Override
	public void onUserDisabled(Long... userIds) {
		if (logger.isDebugEnabled()) {
			logger.debug("User deleted/disabled. ids: "
					+ Arrays.toString(userIds));
		}
		List<Long> ids = Arrays.asList(userIds);
		Collections.sort(ids);
		Iterator<Entry<String, UserProfile>> ite = userProfiles.entrySet()
				.iterator();
		while (ite.hasNext() && !ids.isEmpty()) {
			Entry<String, UserProfile> entry = ite.next();
			UserProfile userProfile = entry.getValue();
			int index = Collections.binarySearch(ids, userProfile
					.getUserSipProfile().getId());
			if (index >= 0) {
				ids.remove(index);
				ite.remove();
			}
		}
	}
}
