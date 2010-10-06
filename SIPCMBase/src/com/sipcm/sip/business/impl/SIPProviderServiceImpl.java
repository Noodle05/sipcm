/**
 * 
 */
package com.sipcm.sip.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractTrackableService;
import com.sipcm.base.dao.DAO;
import com.sipcm.sip.business.SIPProviderService;
import com.sipcm.sip.model.SIPProvider;

/**
 * @author wgao
 * 
 */
@Service("sipProviderService")
@Transactional(readOnly = true)
public class SIPProviderServiceImpl extends
		AbstractTrackableService<SIPProvider, Integer> implements
		SIPProviderService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "sipProviderDao")
	public void setDAO(DAO<SIPProvider, Integer> dao) {
		this.dao = dao;
	}
}
