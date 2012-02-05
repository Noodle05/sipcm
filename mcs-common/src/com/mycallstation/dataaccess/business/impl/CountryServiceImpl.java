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
import com.mycallstation.dataaccess.business.CountryService;
import com.mycallstation.dataaccess.model.Country;

/**
 * @author Wei Gao
 * 
 */
@Service("countryService")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class CountryServiceImpl extends AbstractService<Country, Integer>
		implements CountryService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao.DAO)
	 */
	@Override
	@Resource(name = "countryDAO")
	public void setDAO(DAO<Country, Integer> dao) {
		this.dao = dao;
	}
}
