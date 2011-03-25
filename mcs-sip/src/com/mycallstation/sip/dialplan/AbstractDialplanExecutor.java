/**
 * 
 */
package com.mycallstation.sip.dialplan;

import java.util.Collection;

import javax.annotation.Resource;

import com.mycallstation.dataaccess.business.UserVoipAccountService;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public abstract class AbstractDialplanExecutor implements DialplanExecutor {
	@Resource(name = "userVoipAccountService")
	protected UserVoipAccountService accountService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.dialplan.DialplanExecutor#execute(com.mycallstation
	 * .sip.model. UserSipProfile, java.lang.String)
	 */
	@Override
	public UserVoipAccount execute(UserSipProfile userSipProfile,
			String phoneNumber) {
		Collection<UserVoipAccount> accounts = accountService
				.getOutgoingAccounts(userSipProfile);
		return internalExecute(userSipProfile, phoneNumber, accounts);
	}

	protected abstract UserVoipAccount internalExecute(
			UserSipProfile userSipProfile, String phoneNumber,
			Collection<UserVoipAccount> accounts);
}
