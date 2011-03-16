/**
 * 
 */
package com.mycallstation.sip.business.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.impl.AbstractService;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.common.OnlineStatus;
import com.mycallstation.sip.VoipAccountType;
import com.mycallstation.sip.VoipVendorType;
import com.mycallstation.sip.business.UserVoipAccountService;
import com.mycallstation.sip.dao.UserVoipAccountDAO;
import com.mycallstation.sip.model.UserSipProfile;
import com.mycallstation.sip.model.UserVoipAccount;
import com.mycallstation.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Service("userVoipAccountService")
@Transactional(readOnly = true)
public class UserVoipAccountServiceImpl extends
		AbstractService<UserVoipAccount, Long> implements
		UserVoipAccountService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.impl.AbstractService#setDAO(com.mycallstation
	 * .base.dao .DAO)
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
	 * com.mycallstation.sip.business.UserVoipAccountService#getOutgoingAccounts
	 * (com .mycallstation.sip.model.UserSipProfile)
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
	 * com.mycallstation.sip.business.UserVoipAccountService#getIncomingAccounts
	 * (com .mycallstation.sip.model.UserSipProfile)
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
	 * @see com.mycallstation.sip.business.UserVoipAccountService#
	 * getOnlineIncomingAccounts (com.mycallstation.sip.model.UserSipProfile)
	 */
	@Override
	public Collection<UserVoipAccount> getOnlineIncomingAccounts(
			UserSipProfile user) {
		Filter f1 = filterFactory.createSimpleFilter("owner", user);
		Filter f2 = filterFactory.createSimpleFilter("voipVendor.type",
				VoipVendorType.SIP);
		Filter f3 = filterFactory.createInFilter("type",
				VoipAccountType.INCOME, VoipAccountType.BOTH);
		Filter f4 = filterFactory.createIsNotNullFilter("regExpires");
		Filter filter = f1.appendAnd(f2).appendAnd(f3).appendAnd(f4);
		return dao.getEntities(filter, null, null);
	}

	public Collection<UserVoipAccount> getOfflineIncomingAccounts(
			UserSipProfile user) {
		Filter f1 = filterFactory.createSimpleFilter("owner", user);
		Filter f2 = filterFactory.createSimpleFilter("voipVendor.type",
				VoipVendorType.SIP);
		Filter f3 = filterFactory.createInFilter("type",
				VoipAccountType.INCOME, VoipAccountType.BOTH);
		Filter f4 = filterFactory.createIsNullFilter("regExpires");
		Filter filter = f1.appendAnd(f2).appendAnd(f3).appendAnd(f4);
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserVoipAccountService#
	 * getUserVoipAccountByVendorAndAccount
	 * (com.mycallstation.sip.model.VoipVendor, java.lang.String)
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
		Filter f5 = filterFactory.createIsNotNullFilter("regExpires");
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
	 * @see com.mycallstation.sip.business.UserVoipAccountService#
	 * getOnlineIncomingAccounts (java.lang.Long)
	 */
	@Override
	public Collection<UserVoipAccount> getOnlineIncomingAccounts(Long userId) {
		Filter f1 = filterFactory.createSimpleFilter("owner.id", userId);
		Filter f2 = filterFactory.createSimpleFilter("voipVendor.type",
				VoipVendorType.SIP);
		Filter f3 = filterFactory.createInFilter("type",
				VoipAccountType.INCOME, VoipAccountType.BOTH);
		Filter f4 = filterFactory.createIsNotNullFilter("regExpires");
		Filter filter = f1.appendAnd(f2).appendAnd(f3).appendAnd(f4);
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserVoipAccountService#getUserVoipAccount
	 * (com. mycallstation.sip.model.UserSipProfile)
	 */
	@Override
	public Collection<UserVoipAccount> getUserVoipAccount(
			UserSipProfile userSipProfile) {
		Filter filter = filterFactory.createSimpleFilter("owner",
				userSipProfile);
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserVoipAccountService#updateOnlineStatus
	 * (com. mycallstation.sip.model.UserVoipAccount)
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateRegisterExpires(UserVoipAccount account) {
		((UserVoipAccountDAO) dao).updateRegisterExpires(account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserVoipAccountService#saveAuthResponse
	 * (com.mycallstation .sip.model.UserVoipAccount)
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateAuthResponse(UserVoipAccount account) {
		((UserVoipAccountDAO) dao).updateAuthResponse(account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserVoipAccountService#
	 * updateRegisterExpiresAndAuthResonse
	 * (com.mycallstation.sip.model.UserVoipAccount)
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateRegisterExpiresAndAuthResonse(UserVoipAccount account) {
		((UserVoipAccountDAO) dao)
				.updateRegisterExpiresAndAuthResponse(account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.business.UserVoipAccountService#checkRegisterExpires
	 * ()
	 */
	@Override
	@Transactional(readOnly = false)
	public Collection<Long> checkRegisterExpires(int minExpires) {
		return ((UserVoipAccountDAO) dao).checkRegisterExpires(minExpires);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.business.UserVoipAccountService#
	 * getUserVoipAccountWithAuthResponse(java.lang.Long)
	 */
	@Override
	public UserVoipAccount getUserVoipAccountWithAuthResponse(Long id) {
		UserVoipAccount entity = dao.getEntityById(id);
		entity.getAuthResponse();
		return entity;
	}
}