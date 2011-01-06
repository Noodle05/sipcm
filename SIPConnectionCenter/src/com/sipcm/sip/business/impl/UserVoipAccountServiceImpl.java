/**
 * 
 */
package com.sipcm.sip.business.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.business.impl.AbstractService;
import com.sipcm.base.dao.DAO;
import com.sipcm.base.filter.Filter;
import com.sipcm.sip.VoipAccountType;
import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
@Service("userVoidAccountService")
@Transactional(readOnly = true)
public class UserVoipAccountServiceImpl extends
		AbstractService<UserVoipAccount, Long> implements
		UserVoipAccountService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.business.impl.AbstractService#setDAO(com.sipcm.base.dao
	 * .DAO)
	 */
	@Override
	@Resource(name = "userVoipAccountDAO")
	public void setDAO(DAO<UserVoipAccount, Long> dao) {
		this.dao = dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserVoipAccountService#getOutgoingAccounts(com
	 * .sipcm.sip.model.UserSipProfile)
	 */
	@Override
	public Collection<UserVoipAccount> getOutgoingAccounts(UserSipProfile user) {
		Filter f1 = filterFactory.createSimpleFilter("owner", user);
		Filter f2 = filterFactory.createInFilter("type",
				VoipAccountType.OUTGOING, VoipAccountType.BOTH);
		Filter filter = f1.appendAnd(f2);
		return dao.getEntities(filter, null, null);
	}
}
