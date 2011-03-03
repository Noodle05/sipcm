/**
 * 
 */
package com.sipcm.sip.business.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.base.filter.Filter;
import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.business.VoipVendorService;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Service("voipVendorService")
@Transactional(readOnly = true)
public class VoipVendorServiceImpl extends AbstractService<VoipVendor, Integer>
		implements VoipVendorService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "voipVendorDAO")
	public void setDAO(DAO<VoipVendor, Integer> dao) {
		this.dao = dao;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public VoipVendor createNewEntity() {
		VoipVendor entity = super.createNewEntity();
		entity.setType(VoipVendorType.SIP);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.business.VoipVendorService#getManagableVoipVendors()
	 */
	@Override
	public Collection<VoipVendor> getManagableVoipVendors() {
		Filter filter = filterFactory.createInFilter("type",
				VoipVendorType.GOOGLE_VOICE, VoipVendorType.SIP);
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.VoipVendorService#getVoipVendorByName(java.lang
	 * .String)
	 */
	@Override
	public VoipVendor getVoipVendorByName(String name) {
		Filter filter = filterFactory.createSimpleFilter("name", name);
		return dao.getUniqueEntity(filter);
	}
}
