/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.constant.OnlineStatus;
import com.mycallstation.constant.PhoneNumberStatus;
import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.dao.UserSipProfileDAO;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Service("userSipProfileService")
@Transactional(readOnly = true)
public class UserSipProfileServiceImpl extends
		AbstractService<UserSipProfile, Long> implements UserSipProfileService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
	 */
	@Override
	@Resource(name = "userSipProfileDAO")
	public void setDAO(DAO<UserSipProfile, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.business.Service#createNewEntity()
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserSipProfile createNewEntity() {
		UserSipProfile entity = super.createNewEntity();
		entity.setSipStatus(OnlineStatus.OFFLINE);
		entity.setPhoneNumberStatus(PhoneNumberStatus.UNVERIFIED);
		entity.setAllowLocalDirectly(true);
		entity.setKeepAlive(false);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserSipProfileService#createUserSipProfile
	 * (com .mycallstation.common.model.User)
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserSipProfile createUserSipProfile(User user) {
		UserSipProfile userSipProfile = createNewEntity();
		userSipProfile.setOwner(user);
		return userSipProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserSipProfileService#getUserSipProfileByUser
	 * ( com.mycallstation.common.model.User)
	 */
	@Override
	public UserSipProfile getUserSipProfileByUser(User user) {
		// Filter filter = filterFactory.createSimpleFilter("owner", user);
		// return dao.getUniqueEntity(filter);
		return dao.getEntityById(user.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserSipProfileService#
	 * getUserSipProfileByUsername (java.lang.String)
	 */
	@Override
	public UserSipProfile getUserSipProfileByUsername(String username) {
		Filter filter = filterFactory.createSimpleFilter("owner.username",
				username, Filter.Operator.IEQ);
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserSipProfileService#
	 * getUserSipProfileByPhoneNumber (java.lang.String)
	 */
	@Override
	public UserSipProfile getUserSipProfileByVerifiedPhoneNumber(
			String phoneNumber) {
		Filter f1 = filterFactory
				.createSimpleFilter("phoneNumber", phoneNumber);
		Filter f2 = filterFactory.createSimpleFilter("phoneNumberStatus",
				PhoneNumberStatus.UNVERIFIED, Filter.Operator.NOT_EQ);
		Filter filter = f1.appendAnd(f2);
		List<UserSipProfile> entities = dao.getEntities(filter, null, null);
		if (entities != null && !entities.isEmpty()) {
			return entities.iterator().next();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserSipProfileService#updateOnlineStatus
	 * (com.mycallstation .common.OnlineStatus,
	 * com.mycallstation.sip.model.UserSipProfile[])
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void updateOnlineStatus(OnlineStatus onlineStatus,
			UserSipProfile... userSipProfiles) {
		((UserSipProfileDAO) dao).updateOnlineStatus(onlineStatus,
				userSipProfiles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.dataaccess.business.UserSipProfileService#
	 * updateLastReceiveCallTime
	 * (com.mycallstation.dataaccess.model.UserSipProfile[])
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void updateLastReceiveCallTime(UserSipProfile... userSipProfiles) {
		((UserSipProfileDAO) dao).updateLastReceiveCallTime(userSipProfiles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserSipProfileService#
	 * checkAddressBindingExpires()
	 */
	@Override
	@Transactional(readOnly = false)
	public Collection<Long> checkAddressBindingExpires() {
		return ((UserSipProfileDAO) dao).checkAddressBindingExpires();
	}
}
