/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.dataaccess.business.CountryService;
import com.mycallstation.dataaccess.model.Country;

/**
 * @author Jack
 * 
 */
@Service("countryService")
@Transactional(readOnly = true)
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
