/**
 * 
 */
package com.sipcm.common.business.impl;

import java.util.HashSet;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.jasypt.digest.StringDigester;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.base.filter.Filter;
import com.sipcm.common.AccountStatus;
import com.sipcm.common.business.UserService;
import com.sipcm.common.model.Role;
import com.sipcm.common.model.User;

/**
 * @author Jack
 * 
 */
@Service("userService")
@Transactional(readOnly = true)
public class UserServiceImpl extends AbstractService<User, Long> implements
		UserService {
	public static final String REALM_NAME = "sip.server.realm";

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
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public User createNewEntity() {
		User user = super.createNewEntity();
		user.setStatus(AccountStatus.PENDING);
		user.setRoles(new HashSet<Role>());
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
		Filter filter = filterFactory.createSimpleFilter("username", username,
				Filter.Operator.IEQ);
		return dao.getUniqueEntity(filter);
	}

	@Override
	public User getUserByEmail(String email) {
		Filter filter = filterFactory.createSimpleFilter("email", email,
				Filter.Operator.IEQ);
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
				.append(appConfig.getString(REALM_NAME)).append(":")
				.append(password);
		String passwd = stringDigester.digest(sb.toString()).toLowerCase();
		entity.setPassword(passwd);
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
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getUsername()).append(":")
				.append(appConfig.getString(REALM_NAME)).append(":")
				.append(password);
		return stringDigester.matches(sb.toString(), entity.getPassword());
	}
}
