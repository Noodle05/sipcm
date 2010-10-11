/**
 * 
 */
package com.sipcm.sip.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.sip.business.VoipVenderService;
import com.sipcm.sip.model.VoipVender;

/**
 * @author wgao
 * 
 */
@Service("voipVenderService")
@Transactional(readOnly = true)
public class VoipVenderServiceImpl extends AbstractService<VoipVender, Integer>
		implements VoipVenderService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "voipVenderDAO")
	public void setDAO(DAO<VoipVender, Integer> dao) {
		this.dao = dao;
	}
}
