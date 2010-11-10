/**
 * 
 */
package com.sipcm.sip.dialplan;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * This is a dummy dialplan executor. It will just return the first user voip
 * account.
 * 
 * @author wgao
 */
@Component("dialplanExecutor")
public class CompositeDialplanExecutorImpl extends AbstractDialplanExecutor {
	@Resource(name = "internationalDialplanExecutor")
	private DialplanExecutor internationalDialplanExecutor;

	@Resource(name = "naDialplanExecutor")
	private DialplanExecutor naDialplanExecutor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.dialplan.DialplanExecutor#execute(com.sipcm.sip.model.
	 * UserSipProfile, java.lang.String)
	 */
	@Override
	public UserVoipAccount execute(UserSipProfile userSipProfile,
			String phoneNumber) {
		Set<UserVoipAccount> accounts = userSipProfile.getVoipAccounts();
		if (accounts == null || accounts.isEmpty()) {
			return null;
		}
		if (phoneNumberUtil.isNaPhoneNumber(phoneNumber)) {
			return naDialplanExecutor.execute(userSipProfile, phoneNumber);
		} else {
			return internationalDialplanExecutor.execute(userSipProfile,
					phoneNumber);
		}
	}
}
