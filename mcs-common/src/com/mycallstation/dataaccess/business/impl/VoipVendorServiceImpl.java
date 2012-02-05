/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.constant.VoipVendorType;
import com.mycallstation.dataaccess.business.VoipVendorService;
import com.mycallstation.dataaccess.model.VoipVendor;

/**
 * @author Wei Gao
 * 
 */
@Service("voipVendorService")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
public class VoipVendorServiceImpl extends AbstractService<VoipVendor, Integer>
		implements VoipVendorService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
	 */
	@Override
	@Resource(name = "voipVendorDAO")
	public void setDAO(DAO<VoipVendor, Integer> dao) {
		this.dao = dao;
	}

	@Override
	public VoipVendor createNewEntity() {
		VoipVendor entity = super.createNewEntity();
		entity.setType(VoipVendorType.SIP);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.VoipVendorService#getManagableVoipVendors
	 * ()
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<VoipVendor> getManagableVoipVendors() {
		Filter filter = filterFactory.createInFilter("type",
				VoipVendorType.GOOGLE_VOICE, VoipVendorType.SIP);
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.VoipVendorService#getGoogleVoiceVendor()
	 */
	@Override
	@Transactional(readOnly = true)
	public VoipVendor getGoogleVoiceVendor() {
		Filter filter = filterFactory.createSimpleFilter("type",
				VoipVendorType.GOOGLE_VOICE);
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.VoipVendorService#getSIPVendors()
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<VoipVendor> getSIPVendors() {
		Filter filter = filterFactory.createSimpleFilter("type",
				VoipVendorType.SIP);
		return dao.getEntities(filter, null, null);
	}
}
