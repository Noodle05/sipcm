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
import com.sipcm.common.OnlineStatus;
import com.sipcm.sip.VoipAccountType;
import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserVoipAccountService#getIncomingAccount(com.
	 * sipcm.sip.model.UserSipProfile)
	 */
	@Override
	public UserVoipAccount getIncomingAccount(UserSipProfile user) {
		Filter f1 = filterFactory.createSimpleFilter("owner", user);
		Filter f2 = filterFactory.createInFilter("type",
				VoipAccountType.INCOME, VoipAccountType.BOTH);
		Filter filter = f1.appendAnd(f2);
		Collection<UserVoipAccount> accounts = dao.getEntities(filter, null,
				null);
		if (accounts != null && !accounts.isEmpty()) {
			if (accounts.size() > 1) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"User \"{}\" have more than one incoming account, return first one.",
							user.getDisplayName());
				}
			}
			return accounts.iterator().next();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.business.UserVoipAccountService#
	 * getUserVoipAccountByVendorAndAccount(com.sipcm.sip.model.VoipVendor,
	 * java.lang.String)
	 */
	@Override
	public UserVoipAccount getUserVoipAccountByVendorAndAccount(
			VoipVendor voipVender, String account) {
		Filter f1 = filterFactory.createSimpleFilter("", voipVender);
		Filter f2 = filterFactory.createSimpleFilter("", account);
		Filter f3 = filterFactory.createSimpleFilter("", OnlineStatus.ONLINE);
		Filter f4 = filterFactory.createInFilter("", VoipAccountType.INCOME,
				VoipAccountType.BOTH);
		Filter filter = f1.appendAnd(f2).appendAnd(f3).appendAnd(f4);
		Collection<UserVoipAccount> accounts = dao.getEntities(filter, null,
				null);
		if (accounts != null && !accounts.isEmpty()) {
			if (accounts.size() > 1) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Find multiple user registered with same account \"{}\" on same vender \"{}\". Return only first one.",
							account, voipVender);
				}
			}
			return accounts.iterator().next();
		}
		return null;
	}
}
