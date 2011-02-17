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
import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.dao.UserVoipAccountDAO;
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
	 * com.sipcm.sip.business.UserVoipAccountService#getIncomingAccounts(com
	 * .sipcm.sip.model.UserSipProfile)
	 */
	@Override
	public Collection<UserVoipAccount> getIncomingAccounts(UserSipProfile user) {
		Filter f1 = filterFactory.createSimpleFilter("owner", user);
		Filter f2 = filterFactory.createSimpleFilter("voipVendor.type",
				VoipVendorType.SIP);
		Filter f3 = filterFactory.createInFilter("type",
				VoipAccountType.INCOME, VoipAccountType.BOTH);
		Filter filter = f1.appendAnd(f2).appendAnd(f3);
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserVoipAccountService#getOnlineIncomingAccounts
	 * (com.sipcm.sip.model.UserSipProfile)
	 */
	@Override
	public Collection<UserVoipAccount> getOnlineIncomingAccounts(
			UserSipProfile user) {
		Filter f1 = filterFactory.createSimpleFilter("owner", user);
		Filter f2 = filterFactory.createSimpleFilter("voipVendor.type",
				VoipVendorType.SIP);
		Filter f3 = filterFactory.createInFilter("type",
				VoipAccountType.INCOME, VoipAccountType.BOTH);
		Filter f4 = filterFactory.createSimpleFilter("online", true);
		Filter filter = f1.appendAnd(f2).appendAnd(f3).appendAnd(f4);
		return dao.getEntities(filter, null, null);
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
		Filter f1 = filterFactory.createSimpleFilter("voipVendor", voipVender);
		Filter f2 = filterFactory.createSimpleFilter("account", account);
		Filter f3 = filterFactory.createSimpleFilter("owner.sipStatus",
				OnlineStatus.ONLINE);
		Filter f4 = filterFactory.createInFilter("type",
				VoipAccountType.INCOME, VoipAccountType.BOTH);
		Filter f5 = filterFactory.createSimpleFilter("online", true);
		Filter filter = f1.appendAnd(f2).appendAnd(f3).appendAnd(f4)
				.appendAnd(f5);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.business.UserVoipAccountService#updateOnlineStatus(com.
	 * sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateOnlineStatus(UserVoipAccount account) {
		((UserVoipAccountDAO) dao).updateOnlineStatus(account);
	}
}
