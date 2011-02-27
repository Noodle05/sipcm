/**
 * 
 */
package com.sipcm.sip.dialplan;

import java.util.Collection;

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
@Component("sipDialplanExecutor")
public class CompositeDialplanExecutorImpl extends AbstractDialplanExecutor {
	@Resource(name = "sipInternationalDialplanExecutor")
	private AbstractDialplanExecutor internationalDialplanExecutor;

	@Resource(name = "sipNaDialplanExecutor")
	private AbstractDialplanExecutor naDialplanExecutor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.dialplan.AbstractDialplanExecutor#internalExecute(com.sipcm
	 * .sip.model.UserSipProfile, java.lang.String, java.util.Collection)
	 */
	@Override
	public UserVoipAccount internalExecute(UserSipProfile userSipProfile,
			String phoneNumber, Collection<UserVoipAccount> accounts) {
		if (accounts == null || accounts.isEmpty()) {
			return null;
		}
		if (phoneNumberUtil.isNaPhoneNumber(phoneNumber)) {
			return naDialplanExecutor.internalExecute(userSipProfile,
					phoneNumber, accounts);
		} else {
			return internationalDialplanExecutor.internalExecute(
					userSipProfile, phoneNumber, accounts);
		}
	}
}
