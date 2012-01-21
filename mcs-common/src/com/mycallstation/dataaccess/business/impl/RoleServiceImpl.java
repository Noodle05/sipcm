/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.Operator;
import com.mycallstation.dataaccess.business.RoleService;
import com.mycallstation.dataaccess.model.Role;

/**
 * @author wgao
 * 
 */
@Service("roleService")
public class RoleServiceImpl extends AbstractService<Role, Integer> implements
		RoleService {
	private LoadingCache<String, Role> cache;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.business.impl.AbstractService#init()
	 */
	@Override
	@PostConstruct
	public void init() {
		super.init();
		cache = CacheBuilder.newBuilder().concurrencyLevel(2).softValues()
				.initialCapacity(2).expireAfterWrite(8, TimeUnit.HOURS)
				.build(new CacheLoader<String, Role>() {
					@Override
					public Role load(String key) throws Exception {
						Filter filter = filterFactory.createSimpleFilter(
								"name", key, Operator.IEQ);
						Role role = dao.getUniqueEntity(filter);
						return role;
					}
				});
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
		Role role = cache.getUnchecked(USER_ROLE);
		return role;
	}

	@Override
	public Role getCallerRole() {
		Role role = cache.getUnchecked(CALLER_ROLE);
		return role;
	}

	@Override
	public Role getAdminRole() {
		Role role = cache.getUnchecked(ADMIN_ROLE);
		return role;
	}
}
