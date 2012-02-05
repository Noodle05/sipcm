/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import java.util.HashSet;

import javax.annotation.Resource;

import org.jasypt.digest.StringDigester;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.Operator;
import com.mycallstation.common.BaseConfiguration;
import com.mycallstation.constant.AccountStatus;
import com.mycallstation.dataaccess.business.UserService;
import com.mycallstation.dataaccess.model.Role;
import com.mycallstation.dataaccess.model.User;

/**
 * @author Wei Gao
 * 
 */
@Service("userService")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class UserServiceImpl extends AbstractService<User, Long> implements
		UserService {
	@Resource(name = "globalStringDigester")
	private StringDigester stringDigester;

	@Resource(name = "systemConfiguration")
	private BaseConfiguration appConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao.DAO)
	 */
	@Override
	@Resource(name = "userDAO")
	public void setDAO(DAO<User, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#createNewEntity()
	 */
	@Override
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
	 * com.mycallstation.common.business.UserService#getUserByUsername(java.
	 * lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserByUsername(String username) {
		Filter filter = filterFactory.createSimpleFilter("username", username,
				Operator.IEQ);
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.common.business.UserService#getUserByEmail(java.lang
	 * .String)
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserByEmail(String email) {
		Filter filter = filterFactory.createSimpleFilter("email", email,
				Operator.IEQ);
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.common.business.UserService#setUserPassword(com.
	 * mycallstation.common .model.User, java.lang.String)
	 */
	@Override
	public User setPassword(User entity, String password) {
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getUsername()).append(":")
				.append(appConfig.getRealmName()).append(":").append(password);
		String passwd = stringDigester.digest(sb.toString());
		entity.setPassword(passwd);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.common.business.UserService#matchPassword(com.mycallstation
	 * .common. model.User, java.lang.String)
	 */
	@Override
	public boolean matchPassword(User entity, String password) {
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getUsername()).append(":")
				.append(appConfig.getRealmName()).append(":").append(password);
		return stringDigester.matches(sb.toString(), entity.getPassword());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.common.business.UserService#fullyLoadUser(java.lang
	 * .Long)
	 */
	@Override
	@Transactional(readOnly = true)
	public User fullyLoadUser(Long id) {
		User user = dao.getEntityById(id);
		for (Role role : user.getRoles()) {
			role.getId();
		}
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.dataaccess.business.UserService#fullyLoadUserByUsername
	 * (java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public User fullyLoadUserByUsername(String username) {
		Filter filter = filterFactory.createSimpleFilter("username", username,
				Operator.IEQ);
		User user = dao.getUniqueEntity(filter);
		for (Role role : user.getRoles()) {
			role.getId();
		}
		return user;
	}
}
