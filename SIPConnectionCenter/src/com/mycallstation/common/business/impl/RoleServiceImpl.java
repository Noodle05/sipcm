/**
 * 
 */
package com.mycallstation.common.business.impl;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.MapMaker;
import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.common.business.RoleService;
import com.mycallstation.common.model.Role;

/**
 * @author wgao
 * 
 */
@Service("roleService")
public class RoleServiceImpl extends AbstractService<Role, Integer> implements
		RoleService {
	private ConcurrentMap<String, Role> cache;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.business.impl.AbstractService#init()
	 */
	@Override
	@PostConstruct
	public void init() {
		super.init();
		cache = new MapMaker().concurrencyLevel(2).softValues()
				.initialCapacity(2).expireAfterWrite(8, TimeUnit.HOURS)
				.makeMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
	 */
	@Override
	@Resource(name = "roleDao")
	public void setDAO(DAO<Role, Integer> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.common.business.RoleService#getCallerRule()
	 */
	@Override
	public Role getUserRole() {
		Role role = cache.get(USER_ROLE);
		if (role == null) {
			Filter filter = filterFactory.createSimpleFilter("name", USER_ROLE,
					Filter.Operator.IEQ);
			role = dao.getUniqueEntity(filter);
			if (role != null) {
				cache.putIfAbsent(USER_ROLE, role);
			}
		}
		return role;
	}

	@Override
	public Role getCallerRole() {
		Role role = cache.get(CALLER_ROLE);
		if (role == null) {
			Filter filter = filterFactory.createSimpleFilter("name",
					CALLER_ROLE, Filter.Operator.IEQ);
			role = dao.getUniqueEntity(filter);
			if (role != null) {
				cache.putIfAbsent(CALLER_ROLE, role);
			}
		}
		return role;
	}

	@Override
	public Role getAdminRole() {
		Role role = cache.get(ADMIN_ROLE);
		if (role == null) {
			Filter filter = filterFactory.createSimpleFilter("name",
					ADMIN_ROLE, Filter.Operator.IEQ);
			role = dao.getUniqueEntity(filter);
			if (role != null) {
				cache.putIfAbsent(ADMIN_ROLE, role);
			}
		}
		return role;
	}
}
