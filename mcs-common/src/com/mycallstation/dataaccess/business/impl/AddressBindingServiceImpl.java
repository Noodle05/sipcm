/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.constant.OnlineStatus;
import com.mycallstation.dataaccess.business.AddressBindingService;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.model.AddressBinding;
import com.mycallstation.dataaccess.model.UserSipProfile;

/**
 * @author Wei Gao
 * 
 */
@Service("addressBindingService")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class AddressBindingServiceImpl extends
		AbstractService<AddressBinding, Long> implements AddressBindingService {
	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
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
	 * com.mycallstation.sip.business.AddressBindingService#getAddressBindings
	 * (com.mycallstation .sip.model.UserSipProfile)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AddressBinding> getAddressBindings(UserSipProfile userSipProfile) {
		Filter filter = filterFactory.createSimpleFilter("userSipProfile",
				userSipProfile);
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.AddressBindingService#
	 * createAddressBindingEntity (com.mycallstation.sip.model.UserSipProfile,
	 * javax.servlet.sip.Address, javax.servlet.sip.Address, java.lang.String)
	 */
	@Override
	@Transactional
	public AddressBinding createAddressBindingEntity(
			UserSipProfile userSipProfile, String address, int expires,
			String remoteEnd, String callId, boolean takeItOnline) {
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
	 * com.mycallstation.sip.business.AddressBindingService#removeByUserSipProfile
	 * (com .mycallstation.sip.model.UserSipProfile)
	 */
	@Override
	@Transactional
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
	 * com.mycallstation.sip.business.AddressBindingService#removeBinding(com
	 * .mycallstation. sip.model.AddressBinding,
	 * com.mycallstation.sip.model.UserSipProfile, boolean)
	 */
	@Override
	@Transactional
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
