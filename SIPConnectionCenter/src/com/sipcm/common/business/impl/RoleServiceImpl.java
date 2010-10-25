/**
 * 
 */
package com.sipcm.common.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.common.business.RoleService;
import com.sipcm.common.model.Role;

/**
 * @author wgao
 * 
 */
@Service("roleService")
public class RoleServiceImpl extends AbstractService<Role, Integer> implements
		RoleService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "roleDao")
	public void setDAO(DAO<Role, Integer> dao) {
		this.dao = dao;
	}
}
