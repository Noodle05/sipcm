/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.MapMaker;
import com.sipcm.sip.business.UserSipBindingService;
import com.sipcm.sip.events.RegistrationEventListener;
import com.sipcm.sip.events.RegistrationEventObject;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.util.PhoneNumberUtil;
import com.sipcm.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@Component("sipLocationService")
public class LocationServiceImpl implements LocationService {
	public static final Logger logger = LoggerFactory
			.getLogger(LocationServiceImpl.class);

	private ConcurrentMap<String, UserSipBinding> cache;

	@Resource(name = "sipUtil")
	private transient SipUtil sipUtil;

	@Resource(name = "phoneNumberUtil")
	private PhoneNumberUtil phoneNumberUtil;

	@Resource(name = "userSipBindingService")
	private UserSipBindingService userSipBindingService;

	@Resource(name = "Sip.RegistrationEventListener")
	private RegistrationEventListener listener;

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		cache = new MapMaker().concurrencyLevel(32)
				.expiration(30, TimeUnit.MINUTES).softValues().makeMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#removeAllBinding(java.lang
	 * .String)
	 */
	@Override
	public void removeAllBinding(String key) {
		UserSipBinding up = userSipBindingService.removeByAddress(key);
		cache.remove(key);
		if (up != null) {
			listener.userUnregistered(new RegistrationEventObject(up
					.getUserSipProfile()));
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
	public void updateRegistration(String key, UserSipProfile userSipProfile,
			Address address, Address remoteEnd, String callId) {
		UserSipBinding userSipBinding = null;
		AddressBinding addressBinding = null;
		userSipBinding = getUserSipBindingByKey(key);
		if (userSipBinding != null) {
			addressBinding = getAddressBinding(userSipBinding, address);
		}
		int expires = address.getExpires();

		if (addressBinding != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Find existing binding, will update it. Bind: {}",
						addressBinding);
			}
			if (expires == 0) {
				if (logger.isTraceEnabled()) {
					logger.trace("Remove addess {}", address);
				}
				userSipBinding.getBindings().remove(addressBinding);
				if (userSipBinding.getBindings().isEmpty()) {
					userSipBindingService.removeEntity(userSipBinding);
					cache.remove(key);
					listener.userUnregistered(new RegistrationEventObject(
							userSipBinding.getUserSipProfile()));
				} else {
					userSipBindingService.saveEntity(userSipBinding);
				}
				if (logger.isInfoEnabled()) {
					logger.info("{} deregistered from {}", userSipBinding
							.getUserSipProfile().getDisplayName(), address);
				}
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Update address addess {}", address);
				}
				addressBinding.setAddress(address);
				addressBinding.setRemoteEnd(remoteEnd);
				addressBinding.setCallId(callId);
				addressBinding.setLastCheck(System.currentTimeMillis());
				userSipBindingService.saveEntity(userSipBinding);
			}
		} else {
			if (expires > 0) {
				if (logger.isTraceEnabled()) {
					logger.trace("Add address {}", address);
				}
				boolean exists = true;
				if (userSipBinding == null) {
					exists = false;
					userSipBinding = userSipBindingService.createNewEntity();
				}
				userSipBinding.setAddressOfRecord(key);
				userSipBinding.setUserSipProfile(userSipProfile);
				userSipBindingService.createAddressBindingEntity(
						userSipBinding, address, remoteEnd, callId);
				userSipBindingService.saveEntity(userSipBinding);
				cache.putIfAbsent(key, userSipBinding);

				if (!exists) {
					listener.userRegistered(new RegistrationEventObject(
							userSipBinding.getUserSipProfile()));
				}
				if (logger.isInfoEnabled()) {
					logger.info("{} registered from {}",
							userSipProfile.getDisplayName(), address);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getAddresses(java.lang.
	 * String)
	 */
	@Override
	public Collection<Address> getAddresses(String key) {
		UserSipBinding userSipBinding = getUserSipBindingByKey(key);
		if (userSipBinding != null) {
			Collection<Address> contactHeaders = new ArrayList<Address>(
					userSipBinding.getBindings().size());
			for (AddressBinding binding : userSipBinding.getBindings()) {
				contactHeaders.add(binding.getAddress());
			}
			return contactHeaders;
		} else {
			return Collections.emptyList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getUserProfileByKey(java
	 * .lang .String)
	 */
	@Override
	public UserSipBinding getUserSipBindingByKey(String key) {
		UserSipBinding userSipBinding = cache.get(key);
		if (userSipBinding == null) {
			userSipBinding = userSipBindingService
					.getUserSipBindingByAddress(key);
			if (userSipBinding != null) {
				UserSipBinding up = cache.putIfAbsent(key, userSipBinding);
				if (up != null) {
					userSipBinding = up;
				}
			}
		}
		return userSipBinding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getUserSipBindingByPhoneNumber
	 * (java.lang.String)
	 */
	@Override
	public UserSipBinding getUserSipBindingByPhoneNumber(String phoneNumber) {
		if (phoneNumber == null)
			throw new NullPointerException("Phone number cannot be null.");
		String pn = phoneNumberUtil.getCanonicalizedPhoneNumber(phoneNumber);
		UserSipBinding userSipBinding = null;
		for (UserSipBinding usb : cache.values()) {
			String p = usb.getUserSipProfile().getPhoneNumber() == null ? null
					: usb.getUserSipProfile().getPhoneNumber();
			if (pn.equals(p)) {
				userSipBinding = usb;
				break;
			}
		}
		if (userSipBinding == null) {
			userSipBinding = userSipBindingService
					.getUserSipBindingByAddress(pn);
			if (userSipBinding != null) {
				UserSipBinding usb = cache.putIfAbsent(
						userSipBinding.getAddressOfRecord(), userSipBinding);
				if (usb != null) {
					userSipBinding = usb;
				}
			}
		}
		if (userSipBinding != null) {
			if (!userSipBinding.getUserSipProfile().isAllowLocalDirectly()) {
				userSipBinding = null;
			}
		}
		return userSipBinding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#checkContactExpires(long)
	 */
	@Override
	public void checkContactExpires() {
		try {
			Collection<UserSipBinding> usbs = userSipBindingService
					.checkContactExpires();
			for (UserSipBinding userSipBinding : usbs) {
				cache.remove(userSipBinding.getAddressOfRecord());
			}
			if (!usbs.isEmpty()) {
				Collection<UserSipProfile> usps = new ArrayList<UserSipProfile>(
						usbs.size());
				UserSipProfile[] us = new UserSipProfile[usbs.size()];
				for (UserSipBinding usb : usbs) {
					usps.add(usb.getUserSipProfile());
				}
				us = usps.toArray(us);
				listener.userUnregistered(new RegistrationEventObject(us));
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error happened when process binding expires.", e);
			}
		}
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
		Iterator<Entry<String, UserSipBinding>> ite = cache.entrySet()
				.iterator();
		while (ite.hasNext() && !ids.isEmpty()) {
			Entry<String, UserSipBinding> entry = ite.next();
			if (entry != null) {
				UserSipBinding usb = entry.getValue();
				Long uid = usb.getUserSipProfile().getOwner().getId();
				int index = Collections.binarySearch(ids, uid);
				if (index >= 0) {
					ids.remove(index);
					ite.remove();
				}
			}
		}
	}

	private AddressBinding getAddressBinding(UserSipBinding userSipBinding,
			Address address) {
		URI uri = sipUtil.getCanonicalizedURI(address.getURI());
		for (AddressBinding binding : userSipBinding.getBindings()) {
			Address a = binding.getAddress();
			URI u = sipUtil.getCanonicalizedURI(a.getURI());
			if (uri.equals(u)) {
				return binding;
			}
		}
		return null;
	}
}
