/**
 * 
 */
package com.sipcm.sip.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.sip.business.CallLogService;
import com.sipcm.sip.model.CallLog;

/**
 * @author wgao
 * 
 */
@Service("callLogService")
@Transactional(readOnly = true)
public class CallLogServiceImpl extends AbstractService<CallLog, Long>
		implements CallLogService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "callLogDAO")
	public void setDAO(DAO<CallLog, Long> dao) {
		this.dao = dao;
	}
}
