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
import com.sipcm.sip.business.AddressBindingService;
import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.events.RegistrationEvent;
import com.sipcm.sip.events.RegistrationEventListener;
import com.sipcm.sip.model.AddressBinding;
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

	private ConcurrentMap<UserSipProfile, List<AddressBinding>> cache;
	private volatile boolean cacheBusy = true;

	@Resource(name = "sipUtil")
	private SipUtil sipUtil;

	@Resource(name = "addressBindingService")
	private AddressBindingService addressBindingService;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "sipRegistrationEventListener")
	private RegistrationEventListener listener;

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		cache = new MapMaker().concurrencyLevel(32)
				.expireAfterWrite(30, TimeUnit.MINUTES).softValues().makeMap();
		cacheBusy = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#removeAllBinding(com.sipcm
	 * .sip.model.UserSipProfile)
	 */
	@Override
	public void removeAllBinding(UserSipProfile userSipProfile) {
		cache.remove(userSipProfile);
		addressBindingService.removeByUserSipProfile(userSipProfile);
		listener.userUnregistered(new RegistrationEvent(userSipProfile));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#updateRegistration(com.
	 * sipcm.sip.model.UserSipProfile, javax.servlet.sip.Address,
	 * javax.servlet.sip.Address, java.lang.String)
	 */
	@Override
	public void updateRegistration(UserSipProfile userSipProfile,
			Address address, int expires, Address remoteEnd, String callId) {
		List<AddressBinding> addresses = null;
		AddressBinding addressBinding = null;
		addresses = getUserBinding(userSipProfile, false);
		if (addresses != null) {
			addressBinding = getAddressBinding(addresses, address);
		}
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
					cache.remove(userSipProfile);
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
				addressBinding.setAddress(address);
				addressBinding.setExpires(expires);
				addressBinding.setRemoteEnd(remoteEnd);
				addressBinding.setCallId(callId);
				addressBinding
						.setLastCheck((int) (System.currentTimeMillis() / 1000L));
				addressBindingService.saveEntity(addressBinding);
				Collections.sort(addresses);
				cache.put(userSipProfile, addresses);
			}
		} else {
			if (expires > 0) {
				if (logger.isTraceEnabled()) {
					logger.trace("Add address {}", address);
				}
				boolean exists = true;
				if (addresses == null || addresses.isEmpty()) {
					exists = false;
					if (addresses == null) {
						addresses = new ArrayList<AddressBinding>(1);
					}
				}
				addressBinding = addressBindingService
						.createAddressBindingEntity(userSipProfile, address,
								expires, remoteEnd, callId, !exists);
				addresses.add(addressBinding);
				Collections.sort(addresses);
				cache.put(userSipProfile, addresses);

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
	 * com.sipcm.sip.locationservice.LocationService#getAddresses(com.sipcm.
	 * sip.model.UserSipProfile)
	 */
	@Override
	public Collection<Address> getAddresses(UserSipProfile userSipProfile) {
		List<AddressBinding> addresses = getUserBinding(userSipProfile);
		if (addresses != null && !addresses.isEmpty()) {
			Collection<Address> contactHeaders = new ArrayList<Address>(
					addresses.size());
			int now = (int) (System.currentTimeMillis() / 1000L);
			for (AddressBinding binding : addresses) {
				Address a = (Address) binding.getAddress().clone();
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
	 * com.sipcm.sip.locationservice.LocationService#getUserProfileByKey(java
	 * .lang .String)
	 */
	@Override
	public List<AddressBinding> getUserBinding(UserSipProfile userSipProfile) {
		return getUserBinding(userSipProfile, true);
	}

	private List<AddressBinding> getUserBinding(UserSipProfile userSipProfile,
			boolean useCache) {
		List<AddressBinding> addresses = null;
		if (useCache && !cacheBusy) {
			addresses = cache.get(userSipProfile);
		}
		if (addresses == null) {
			addresses = addressBindingService
					.getAddressBindings(userSipProfile);
			if (addresses != null && !addresses.isEmpty()) {
				Collections.sort(addresses);
				if (useCache && !cacheBusy) {
					List<AddressBinding> a = cache.putIfAbsent(userSipProfile,
							addresses);
					if (a != null) {
						addresses = a;
					}
				}
			}
		}
		return addresses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.LocationService#getUserSipBindingByPhoneNumber
	 * (java.lang.String)
	 */
	@Override
	public List<AddressBinding> getUserSipBindingByPhoneNumber(
			String phoneNumber) {
		if (phoneNumber == null)
			throw new NullPointerException("Phone number cannot be null.");
		String pn = PhoneNumberUtil.getCanonicalizedPhoneNumber(phoneNumber);
		List<AddressBinding> addresses = null;
		if (!cacheBusy) {
			for (Entry<UserSipProfile, List<AddressBinding>> entry : cache
					.entrySet()) {
				UserSipProfile usp = entry.getKey();
				String p = usp.getPhoneNumberStatus().isVerified()
						&& usp.getPhoneNumber() == null ? null : usp
						.getPhoneNumber();
				if (pn.equals(p)) {
					if (usp.isAllowLocalDirectly()) {
						addresses = entry.getValue();
						break;
					} else {
						return null;
					}
				}
			}
		}
		if (addresses == null) {
			UserSipProfile usp = userSipProfileService
					.getUserSipProfileByPhoneNumber(pn);
			if (usp != null) {
				addresses = addressBindingService.getAddressBindings(usp);
				if (addresses != null && !addresses.isEmpty() && !cacheBusy) {
					List<AddressBinding> a = cache.putIfAbsent(usp, addresses);
					if (a != null) {
						addresses = a;
					}
				}
				if (!usp.isAllowLocalDirectly()) {
					addresses = null;
				}
			}
		}
		return addresses;
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
						cache.remove(u);
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
	 * com.sipcm.sip.locationservice.LocationService#onUserChanged(java.lang
	 * .Long[])
	 */
	@Override
	public void onUserChanged(Long... userIds) {
		if (logger.isDebugEnabled()) {
			logger.debug("User changed! ids: " + Arrays.toString(userIds));
		}
		List<Long> ids = new ArrayList<Long>(Arrays.asList(userIds));
		Collections.sort(ids);
		Iterator<Entry<UserSipProfile, List<AddressBinding>>> ite = cache
				.entrySet().iterator();
		while (ite.hasNext() && !ids.isEmpty()) {
			Entry<UserSipProfile, List<AddressBinding>> entry = ite.next();
			if (entry != null) {
				UserSipProfile usp = entry.getKey();
				Long uid = usp.getOwner().getId();
				int index = Collections.binarySearch(ids, uid);
				if (index >= 0) {
					ids.remove(index);
					ite.remove();
				}
			}
		}
	}

	private AddressBinding getAddressBinding(
			Collection<AddressBinding> addresses, Address address) {
		URI uri = sipUtil.getCanonicalizedURI(address.getURI());
		for (AddressBinding binding : addresses) {
			Address a = binding.getAddress();
			URI u = sipUtil.getCanonicalizedURI(a.getURI());
			if (uri.equals(u)) {
				return binding;
			}
		}
		return null;
	}
}
