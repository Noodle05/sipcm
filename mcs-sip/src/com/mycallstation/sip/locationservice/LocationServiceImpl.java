/**
 * 
 */
package com.mycallstation.sip.locationservice;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.mycallstation.dataaccess.business.AddressBindingService;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.model.AddressBinding;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.sip.events.RegistrationEvent;
import com.mycallstation.sip.events.RegistrationEventListener;
import com.mycallstation.sip.util.SipAddressComparator;
import com.mycallstation.sip.util.SipConfiguration;
import com.mycallstation.sip.util.SipUtil;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
@Component("sipLocationService")
public class LocationServiceImpl implements LocationService {
	public static final Logger logger = LoggerFactory
			.getLogger(LocationServiceImpl.class);

	private Cache<UserSipProfile, AddressBindings> cache;

	@Resource(name = "sipUtil")
	private SipUtil sipUtil;

	@Resource(name = "addressBindingService")
	private AddressBindingService addressBindingService;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "sipRegistrationEventListener")
	private RegistrationEventListener listener;

	@Resource(name = "sipAddressComparator")
	private SipAddressComparator sipAddressComparator;

	@Resource(name = "systemConfiguration")
	private SipConfiguration appConfig;

	private int minimumExpires;

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		cache = CacheBuilder.newBuilder().concurrencyLevel(8)
				.expireAfterWrite(30, TimeUnit.MINUTES).softValues()
				.maximumSize(500)
				.build(new CacheLoader<UserSipProfile, AddressBindings>() {
					@Override
					public AddressBindings load(UserSipProfile key)
							throws Exception {
						List<AddressBinding> addresses = addressBindingService
								.getAddressBindings(key);
						return new AddressBindings(addresses,
								sipAddressComparator);
					}
				});
		minimumExpires = appConfig.getSipMinExpires() / 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.locationservice.LocationService#removeAllBinding
	 * (com.mycallstation .sip.model.UserSipProfile)
	 */
	@Override
	public void removeAllBinding(UserSipProfile userSipProfile) {
		cache.invalidate(userSipProfile);
		addressBindingService.removeByUserSipProfile(userSipProfile);
		listener.userUnregistered(new RegistrationEvent(userSipProfile));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.locationservice.LocationService#updateRegistration
	 * (com. mycallstation.sip.model.UserSipProfile, javax.servlet.sip.Address,
	 * javax.servlet.sip.Address, java.lang.String)
	 */
	@Override
	public void updateRegistration(UserSipProfile userSipProfile,
			Address address, int expires, Address remoteEnd, String callId)
			throws LocationServiceException {
		cache.invalidate(userSipProfile);
		AddressBindings addresses = cache.getUnchecked(userSipProfile);
		addresses.updateRegistration(userSipProfile, address, expires,
				remoteEnd, callId);
	}

	private void internalUpdateRegistration(UserSipProfile userSipProfile,
			List<AddressBinding> addresses, Address address, int expires,
			Address remoteEnd, String callId) throws LocationServiceException {
		AddressBinding addressBinding = getAddressBinding(addresses, address);
		if (addressBinding != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Find existing binding, will update it. Bind: {}",
						addressBinding);
			}
			if (expires == 0) {
				if (logger.isTraceEnabled()) {
					logger.trace("Remove addess {}", address);
				}
				addresses.remove(addressBinding);
				if (addresses.isEmpty()) {
					addressBindingService.removeBinding(addressBinding,
							userSipProfile, true);
					cache.invalidate(userSipProfile);
					listener.userUnregistered(new RegistrationEvent(
							userSipProfile));
				} else {
					addressBindingService.removeBinding(addressBinding,
							userSipProfile, false);
				}
				if (logger.isInfoEnabled()) {
					logger.info("{} deregistered from {}",
							userSipProfile.getDisplayName(), address);
				}
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Update address addess {}", addressBinding);
				}
				int now = (int) (System.currentTimeMillis() / 1000L);
				if (appConfig.isRefuseBriefRegister()
						&& (addressBinding.getExpires() - (now - addressBinding
								.getLastCheck())) > minimumExpires) {
					throw new RegisterTooFrequentException();
				}
				addressBinding.setAddress(sipUtil.sipAddressToString(address));
				addressBinding.setExpires(expires);
				addressBinding.setRemoteEnd(sipUtil
						.sipAddressToString(remoteEnd));
				addressBinding.setCallId(callId);
				addressBinding
						.setLastCheck((int) (System.currentTimeMillis() / 1000L));
				addressBindingService.saveEntity(addressBinding);
				Collections.sort(addresses, sipAddressComparator);
				listener.userRenewRegistration(new RegistrationEvent(
						userSipProfile));
			}
		} else {
			if (expires > 0) {
				if (logger.isTraceEnabled()) {
					logger.trace("Add address {}", address);
				}
				boolean exists = true;
				if (addresses.isEmpty()) {
					exists = false;
				}
				addressBinding = addressBindingService
						.createAddressBindingEntity(userSipProfile,
								sipUtil.sipAddressToString(address), expires,
								sipUtil.sipAddressToString(remoteEnd), callId,
								!exists);
				addresses.add(addressBinding);
				Collections.sort(addresses, sipAddressComparator);

				if (!exists) {
					listener.userRegistered(new RegistrationEvent(
							userSipProfile));
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
	 * com.mycallstation.sip.locationservice.LocationService#getAddresses(com
	 * .mycallstation. sip.model.UserSipProfile)
	 */
	@Override
	public Collection<Address> getAddresses(UserSipProfile userSipProfile) {
		List<AddressBinding> addresses = getUserBinding(userSipProfile);
		if (addresses != null && !addresses.isEmpty()) {
			Collection<Address> contactHeaders = new ArrayList<Address>(
					addresses.size());
			int now = (int) (System.currentTimeMillis() / 1000L);
			for (AddressBinding binding : addresses) {
				Address t = sipUtil.stringToSipAddress(binding.getAddress());
				Address a = (Address) t.clone();
				a.setExpires(binding.getExpires()
						- (now - binding.getLastCheck()));
				contactHeaders.add(a);
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
	 * com.mycallstation.sip.locationservice.LocationService#getUserProfileByKey
	 * (java .lang .String)
	 */
	@Override
	public List<AddressBinding> getUserBinding(UserSipProfile userSipProfile) {
		return cache.getUnchecked(userSipProfile).getAddressBindings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.locationservice.LocationService#
	 * getUserSipBindingByPhoneNumber (java.lang.String)
	 */
	@Override
	public List<AddressBinding> getUserSipBindingByPhoneNumber(
			String phoneNumber) {
		if (phoneNumber == null)
			throw new NullPointerException("Phone number cannot be null.");
		String pn = PhoneNumberUtil.getCanonicalizedPhoneNumber(phoneNumber);
		List<AddressBinding> addresses = null;
		UserSipProfile userSipProfile = null;
		for (UserSipProfile usp : cache.asMap().keySet()) {
			String p = usp.getPhoneNumberStatus().isVerified()
					&& usp.getPhoneNumber() == null ? null : usp
					.getPhoneNumber();
			if (pn.equals(p)) {
				userSipProfile = usp;
				break;
			}
		}
		if (userSipProfile == null) {
			userSipProfile = userSipProfileService
					.getUserSipProfileByVerifiedPhoneNumber(pn);
		}
		if (userSipProfile != null && userSipProfile.isAllowLocalDirectly()) {
			addresses = cache.getUnchecked(userSipProfile).getAddressBindings();
		}
		return addresses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.locationservice.LocationService#checkContactExpires
	 * (long)
	 */
	@Override
	public void checkContactExpires() {
		try {
			Collection<Long> uspids = null;
			uspids = userSipProfileService.checkAddressBindingExpires();
			if (uspids != null && !uspids.isEmpty()) {
				if (logger.isDebugEnabled()) {
					Long[] ids = uspids.toArray(new Long[uspids.size()]);
					logger.debug("User expired: {}", Arrays.toString(ids));
				}
				for (Long id : uspids) {
					UserSipProfile u = userSipProfileService.getEntityById(id);
					if (u != null) {
						cache.invalidate(u);
						listener.userUnregistered(new RegistrationEvent(u));
					}
				}
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
	 * com.mycallstation.sip.locationservice.LocationService#onUserChanged(java
	 * .lang .Long[])
	 */
	@Override
	public void onUserChanged(Long... userIds) {
		if (userIds == null) {
			throw new NullPointerException("User ids cannot be null.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("User changed! ids: " + Arrays.toString(userIds));
		}
		List<Long> ids = new ArrayList<Long>(Arrays.asList(userIds));
		Collections.sort(ids);
		Iterator<UserSipProfile> ite = cache.asMap().keySet().iterator();
		while (ite.hasNext() && !ids.isEmpty()) {
			UserSipProfile usp = ite.next();
			Long uid = usp.getId();
			int index = Collections.binarySearch(ids, uid);
			if (index >= 0) {
				ids.remove(index);
				cache.invalidate(usp);
			}
		}
	}

	private AddressBinding getAddressBinding(
			Collection<AddressBinding> addresses, Address address) {
		URI uri = sipUtil.getCanonicalizedURI(address.getURI());
		for (AddressBinding binding : addresses) {
			Address a = sipUtil.stringToSipAddress(binding.getAddress());
			URI u = sipUtil.getCanonicalizedURI(a.getURI());
			if (uri.equals(u)) {
				return binding;
			}
		}
		return null;
	}

	private class AddressBindings {
		private final List<AddressBinding> data;
		private final Lock readLock;
		private final Lock writeLock;

		private AddressBindings(List<AddressBinding> data,
				Comparator<AddressBinding> comparator) {
			this.data = data == null ? new ArrayList<AddressBinding>() : data;
			ReadWriteLock rwl = new ReentrantReadWriteLock();
			readLock = rwl.readLock();
			writeLock = rwl.writeLock();
			sortIt();
		}

		private void sortIt() {
			Collections.sort(data, sipAddressComparator);
		}

		private List<AddressBinding> getAddressBindings() {
			readLock.lock();
			try {
				return Collections
						.unmodifiableList(new ArrayList<AddressBinding>(data));
			} finally {
				readLock.unlock();
			}
		}

		private void updateRegistration(UserSipProfile userSipProfile,
				Address address, int expires, Address remoteEnd, String callId)
				throws LocationServiceException {
			writeLock.lock();
			try {
				internalUpdateRegistration(userSipProfile, data, address,
						expires, remoteEnd, callId);
			} finally {
				writeLock.unlock();
			}
		}
	}
}
