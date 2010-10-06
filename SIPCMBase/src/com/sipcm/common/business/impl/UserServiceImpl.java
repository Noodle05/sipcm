/**
 * 
 */
package com.sipcm.common.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractTrackableService;
import com.sipcm.base.dao.DAO;
import com.sipcm.base.filter.Filter;
import com.sipcm.common.AccountStatus;
import com.sipcm.common.OnlineStatus;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;

/**
 * @author Jack
 * 
 */
@Service("userService")
@Transactional(readOnly = true)
public class UserServiceImpl extends AbstractTrackableService<User, Long>
		implements UserService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm
	 * .base.dao.DAO)
	 */
	@Override
	@Resource(name = "userDAO")
	public void setDAO(DAO<User, Long> dao) {
		this.dao = dao;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public User createNewEntity() {
		User user = super.createNewEntity();
		user.setStatus(AccountStatus.PENDING);
		user.setSipStatus(OnlineStatus.OFFLINE);
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.common.business.UserService#getUserByUsername(java.lang.String)
	 */
	@Override
	public User getUserByUsername(String username) {
		Filter filter = filterFactory.createSimpleFilter("username", username);
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.common.business.UserService#getUserBySipId(java.lang.String)
	 */
	@Override
	public User getUserBySipId(String sipId) {
		Filter filter = filterFactory.createSimpleFilter("sipId", sipId);
		return dao.getUniqueEntity(filter);
	}
}
