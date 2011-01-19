/**
 * 
 */
package com.sipcm.sip.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.sip.Address;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.Callback;
import com.sipcm.base.dao.DAO;
import com.sipcm.base.filter.Filter;
import com.sipcm.sip.business.UserSipBindingService;
import com.sipcm.sip.dao.UserSipBindingDAO;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipBinding;

/**
 * @author wgao
 * 
 */
@Service("userSipBindingService")
@Transactional(readOnly = true)
public class UserSipBindingServiceImpl extends
		AbstractService<UserSipBinding, Long> implements UserSipBindingService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "userSipBindingDAO")
	public void setDAO(DAO<UserSipBinding, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.business.impl.AbstractService#createNewEntity()
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public UserSipBinding createNewEntity() {
		UserSipBinding entity = super.createNewEntity();
		entity.setBindings(new TreeSet<AddressBinding>());
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserSipBindingService#removeByAddress(java.lang
	 * .String)
	 */
	@Override
	@Transactional(readOnly = false)
	public UserSipBinding removeByAddress(String address) {
		UserSipBinding userSipBinding = getUserSipBindingByAddress(address);
		if (userSipBinding != null) {
			dao.removeEntity(userSipBinding);
		}
		return userSipBinding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserSipBindingService#getUserSipBindingByAddress
	 * (java.lang.String)
	 */
	@Override
	public UserSipBinding getUserSipBindingByAddress(String address) {
		Filter filter = filterFactory.createSimpleFilter("addressOfRecord",
				address);
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.business.UserSipBindingService#checkContactExpires()
	 */
	@Override
	@Transactional(readOnly = false)
	public Collection<UserSipBinding> checkContactExpires() {
		final Collection<UserSipBinding> ret = new ArrayList<UserSipBinding>();
		final long now = System.currentTimeMillis();
		((UserSipBindingDAO) dao)
				.goThoughAllEntity(new Callback<UserSipBinding, Long>() {
					@Override
					public void execute(DAO<UserSipBinding, Long> dao,
							UserSipBinding userSipBinding) {
						Iterator<AddressBinding> i = userSipBinding
								.getBindings().iterator();
						while (i.hasNext()) {
							AddressBinding ab = i.next();
							int time = (int) ((now - ab.getLastCheck()) / 1000L);
							if ((ab.getAddress().getExpires() - time) <= -60) {
								i.remove();
							} else {
								ab.getAddress().setExpires(
										ab.getAddress().getExpires() - time);
								ab.setLastCheck(now);
							}
						}
						if (userSipBinding.getBindings().isEmpty()) {
							dao.removeEntity(userSipBinding);
							ret.add(userSipBinding);
						} else {
							dao.saveEntity(userSipBinding);
						}
					}
				});
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserSipBindingService#createAddressBindingEntity
	 * (com.sipcm.sip.model.UserSipProfile, javax.servlet.sip.Address,
	 * javax.servlet.sip.Address, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public AddressBinding createAddressBindingEntity(
			UserSipBinding userSipBinding, Address address, Address remoteEnd,
			String callId) {
		AddressBinding addressBinding = new AddressBinding();
		addressBinding.setAddress(address);
		addressBinding.setCallId(callId);
		addressBinding.setRemoteEnd(remoteEnd);
		addressBinding.setUserSipBinding(userSipBinding);
		addressBinding.setLastCheck(System.currentTimeMillis());
		if (userSipBinding.getBindings().contains(addressBinding)) {
			if (logger.isWarnEnabled()) {
				for (AddressBinding ab : userSipBinding.getBindings()) {
					logger.info(
							"Address Binding \"{}\" equals \"{}\"? {}",
							new Object[] { addressBinding, ab,
									addressBinding.equals(ab) });
				}
			}
		}
		if (!userSipBinding.getBindings().add(addressBinding)) {
			if (logger.isWarnEnabled()) {
				logger.warn("What's the fxxk?");
			}
		}
		return addressBinding;
	}
}
