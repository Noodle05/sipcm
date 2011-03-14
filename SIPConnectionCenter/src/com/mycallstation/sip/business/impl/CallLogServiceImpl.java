/**
 * 
 */
package com.mycallstation.sip.business.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.sip.business.CallLogService;
import com.mycallstation.sip.model.CallLog;

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
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
	 */
	@Override
	@Resource(name = "callLogDAO")
	public void setDAO(DAO<CallLog, Long> dao) {
		this.dao = dao;
	}
}
