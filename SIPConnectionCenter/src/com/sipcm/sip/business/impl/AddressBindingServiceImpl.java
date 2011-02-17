/**
 * 
 */
package com.sipcm.sip.business.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.sip.Address;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.base.filter.Filter;
import com.sipcm.common.OnlineStatus;
import com.sipcm.sip.business.AddressBindingService;
import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Service("addressBindingService")
public class AddressBindingServiceImpl extends
		AbstractService<AddressBinding, Long> implements AddressBindingService {
	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "addressBindingDAO")
	public void setDAO(DAO<AddressBinding, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.AddressBindingService#getAddressBindings(com.sipcm
	 * .sip.model.UserSipProfile)
	 */
	@Override
	public List<AddressBinding> getAddressBindings(UserSipProfile userSipProfile) {
		Filter filter = filterFactory.createSimpleFilter("userSipProfile",
				userSipProfile);
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.AddressBindingService#createAddressBindingEntity
	 * (com.sipcm.sip.model.UserSipProfile, javax.servlet.sip.Address,
	 * javax.servlet.sip.Address, java.lang.String)
	 */
	@Override
	@Transactional(readOnly = false)
	public AddressBinding createAddressBindingEntity(
			UserSipProfile userSipProfile, Address address, int expires,
			Address remoteEnd, String callId, boolean takeItOnline) {
		AddressBinding addressBinding = super.createNewEntity();
		addressBinding.setAddress(address);
		addressBinding.setExpires(expires);
		addressBinding.setCallId(callId);
		addressBinding.setRemoteEnd(remoteEnd);
		addressBinding.setUserSipProfile(userSipProfile);
		addressBinding.setLastCheck((int) (System.currentTimeMillis() / 1000L));
		dao.saveEntity(addressBinding);
		if (takeItOnline) {
			userSipProfileService.updateOnlineStatus(OnlineStatus.ONLINE,
					userSipProfile);
		}
		return addressBinding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.AddressBindingService#removeByUserSipProfile(com
	 * .sipcm.sip.model.UserSipProfile)
	 */
	@Override
	@Transactional(readOnly = false)
	public void removeByUserSipProfile(UserSipProfile userSipProfile) {
		Collection<AddressBinding> address = getAddressBindings(userSipProfile);
		if (address != null && !address.isEmpty()) {
			dao.removeEntities(address);
		}
		userSipProfileService.updateOnlineStatus(OnlineStatus.OFFLINE,
				userSipProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.AddressBindingService#removeBinding(com.sipcm.
	 * sip.model.AddressBinding, com.sipcm.sip.model.UserSipProfile, boolean)
	 */
	@Override
	@Transactional(readOnly = false)
	public AddressBinding removeBinding(AddressBinding binding,
			UserSipProfile userSipProfile, boolean takeItOffline) {
		AddressBinding ret;
		if (takeItOffline) {
			// Set online status to offline will trigger the trigger to delete
			// all binding
			userSipProfileService.updateOnlineStatus(OnlineStatus.OFFLINE,
					userSipProfile);
			ret = binding;
		} else {
			ret = dao.removeEntity(binding);
		}
		return ret;
	}
}
