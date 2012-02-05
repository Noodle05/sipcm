/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.dataaccess.business.AddressService;
import com.mycallstation.dataaccess.model.Address;

/**
 * @author Wei Gao
 * 
 */
@Service("addressService")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class AddressServiceImpl extends AbstractService<Address, Long>
		implements AddressService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao.DAO)
	 */
	@Override
	@Resource(name = "addressDAO")
	public void setDAO(DAO<Address, Long> dao) {
		this.dao = dao;
	}
}
