/**
 * 
 */
package com.sipcm.sip.dialplan;

import java.util.Collection;

import javax.annotation.Resource;

import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
public abstract class AbstractDialplanExecutor implements DialplanExecutor {
	@Resource(name = "phoneNumberUtil")
	protected PhoneNumberUtil phoneNumberUtil;

	@Resource(name = "userVoidAccountService")
	protected UserVoipAccountService accountService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.dialplan.DialplanExecutor#execute(com.sipcm.sip.model.
	 * UserSipProfile, java.lang.String)
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
