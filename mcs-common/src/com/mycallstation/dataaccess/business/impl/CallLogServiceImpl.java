/**
 * 
 */
package com.mycallstation.dataaccess.business.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.dataaccess.business.CallLogService;
import com.mycallstation.dataaccess.model.CallLog;

/**
 * @author Wei Gao
 * 
 */
@Service("callLogService")
@Scope(value = BeanDefinition.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.INTERFACES)
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
