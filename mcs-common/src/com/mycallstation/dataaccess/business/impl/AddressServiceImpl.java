/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.dataaccess.business.AddressService;
import com.mycallstation.dataaccess.model.Address;

/**
 * @author Jack
 * 
 */
@Service("addressService")
@Transactional(readOnly = true)
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
