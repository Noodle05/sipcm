/**
 * 
 */
package com.sipcm.common.business.impl;

import javax.annotation.Resource;

import org.apache.catalina.realm.RealmBase;
import org.apache.commons.configuration.Configuration;
import org.jasypt.digest.StringDigester;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
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
public class UserServiceImpl extends AbstractService<User, Long> implements
		UserService {
	public static final String DOMAIN_NAME = "sip.server.realm";
	@Resource(name = "stringDigester")
	private StringDigester stringDigester;

	@Resource(name = "applicationConfiguration")
	private Configuration appConfig;

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
	 * com.sipcm.common.business.UserService#setUserPassword(com.sipcm.common
	 * .model.User, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public User setPassword(User entity, String password) {
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getUsername()).append(":")
				.append(appConfig.getString(DOMAIN_NAME)).append(":")
				.append(password);
		String passwd = RealmBase.Digest(sb.toString(), "MD5", null);
		entity.setPassword(stringDigester.digest(passwd));
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.common.business.UserService#matchPassword(com.sipcm.common.
	 * model.User, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public boolean matchPassword(User entity, String password) {
		return stringDigester.matches(password, entity.getPassword());
	}
}