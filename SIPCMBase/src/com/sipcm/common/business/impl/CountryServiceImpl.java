/**
 * 
 */
package com.sipcm.common.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractTrackableService;
import com.sipcm.base.dao.DAO;
import com.sipcm.common.business.CountryService;
import com.sipcm.common.model.Country;

/**
 * @author Jack
 * 
 */
@Service("countryService")
@Transactional(readOnly = true)
public class CountryServiceImpl extends
		AbstractTrackableService<Country, Integer> implements CountryService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm
	 * .base.dao.DAO)
	 */
	@Override
	@Resource(name = "countryDAO")
	public void setDAO(DAO<Country, Integer> dao) {
		this.dao = dao;
	}
}
