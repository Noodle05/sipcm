/**
 * 
 */
package com.sipcm.common.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.common.business.UserActivationService;
import com.sipcm.common.model.User;
import com.sipcm.common.model.UserActivation;
import com.sipcm.util.StringUtils;

/**
 * @author wgao
 * 
 */
@Service("userActivationService")
@Transactional(readOnly = true)
public class UserActivationServiceImpl extends
		AbstractService<UserActivation, Long> implements UserActivationService {
	private static final int ACTIVE_CODE_LENGTH = 32;

	@Resource(name = "stringUtils")
	private StringUtils stringUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "userActivationDAO")
	public void setDAO(DAO<UserActivation, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.business.impl.AbstractService#createNewEntity()
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserActivation createNewEntity() {
		UserActivation entity = super.createNewEntity();
		entity.setActiveCode(stringUtils
				.generateRandomString(ACTIVE_CODE_LENGTH));
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.common.business.UserActivationService#createUserActivation(
	 * com.sipcm.common.model.User)
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserActivation createUserActivation(User owner) {
		UserActivation entity = createNewEntity();
		entity.setOwner(owner);
		return entity;
	}
}
