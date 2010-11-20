/**
 * 
 */
package com.sipcm.sip.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.base.filter.Filter;
import com.sipcm.common.OnlineStatus;
import com.sipcm.common.PhoneNumberStatus;
import com.sipcm.common.model.User;
import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.model.UserSipProfile;

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
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "userSipProfileDAO")
	public void setDAO(DAO<UserSipProfile, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.business.Service#createNewEntity()
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public UserSipProfile createNewEntity() {
		UserSipProfile entity = super.createNewEntity();
		entity.setSipStatus(OnlineStatus.OFFLINE);
		entity.setPhoneNumberStatus(PhoneNumberStatus.UNVERIFIED);
		entity.setAllowLocalDirectly(true);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserSipProfileService#createUserSipProfile(com
	 * .sipcm.common.model.User)
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public UserSipProfile createUserSipProfile(User user) {
		UserSipProfile userSipProfile = createNewEntity();
		userSipProfile.setOwner(user);
		return userSipProfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserSipProfileService#getUserSipProfileByUsername
	 * (java.lang.String)
	 */
	@Override
	public UserSipProfile getUserSipProfileByUsername(String username) {
		Filter filter = filterFactory.createSimpleFilter("owner.username",
				username);
		return dao.getUniqueEntity(filter);
	}
}