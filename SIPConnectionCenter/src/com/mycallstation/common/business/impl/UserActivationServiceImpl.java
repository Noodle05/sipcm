/**
 * 
 */
package com.mycallstation.common.business.impl;

import java.util.Calendar;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.common.ActiveMethod;
import com.mycallstation.common.business.UserActivationService;
import com.mycallstation.common.model.User;
import com.mycallstation.common.model.UserActivation;
import com.mycallstation.util.StringUtils;

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
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
	 */
	@Override
	@Resource(name = "userActivationDAO")
	public void setDAO(DAO<UserActivation, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#createNewEntity()
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
	 * com.mycallstation.common.business.UserActivationService#createUserActivation
	 * ( com.mycallstation.common.model.User,
	 * com.mycallstation.common.ActiveMethod)
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserActivation createUserActivation(final User owner,
			final ActiveMethod method, int expireHours) {
		if (method == null) {
			throw new NullPointerException("Active method cannot be null.");
		}
		if (owner == null) {
			throw new NullPointerException("Owner cannot be null.");
		}
		if (ActiveMethod.NONE.equals(method)) {
			throw new IllegalArgumentException(
					"Cannot create user activation for method \"NONE\".");
		}
		if (expireHours <= 0) {
			throw new IllegalArgumentException(
					"Activiation code expire hours cannot less/equal than 0.");
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, expireHours);
		UserActivation entity = createNewEntity();
		entity.setOwner(owner);
		entity.setMethod(method);
		entity.setExpireDate(c.getTime());
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.common.business.UserActivationService#
	 * getUserActivationByUser (com.mycallstation.common.model.User)
	 */
	@Override
	public UserActivation getUserActivationByUser(final User user) {
		Filter filter = filterFactory.createSimpleFilter("owner.id",
				user.getId());
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.common.business.UserActivationService#updateExpires
	 * (com.mycallstation .common.model.UserActivation, int)
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserActivation updateExpires(final UserActivation userActivaiton,
			int expireHours) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, expireHours);
		userActivaiton.setExpireDate(c.getTime());
		return userActivaiton;
	}
}
