/**
 * 
 */
package com.sipcm.common.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractTrackableService;
import com.sipcm.base.dao.DAO;
import com.sipcm.common.business.AddressService;
import com.sipcm.common.model.Address;

/**
 * @author Jack
 * 
 */
@Service("addressService")
@Transactional(readOnly = true)
public class AddressServiceImpl extends AbstractTrackableService<Address, Long>
		implements AddressService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm
	 * .base.dao.DAO)
	 */
	@Override
	@Resource(name = "addressDAO")
	public void setDAO(DAO<Address, Long> dao) {
		this.dao = dao;
	}
}
