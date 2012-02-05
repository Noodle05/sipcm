/**
 * 
 */
package com.mycallstation.sip.dialplan;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * This is a dummy dialplan executor. It will just return the first user voip
 * account.
 * 
 * @author Wei Gao
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
	 * com.mycallstation.sip.dialplan.AbstractDialplanExecutor#internalExecute
	 * (com.mycallstation .sip.model.UserSipProfile, java.lang.String,
	 * java.util.Collection)
	 */
	@Override
	public UserVoipAccount internalExecute(UserSipProfile userSipProfile,
			String phoneNumber, Collection<UserVoipAccount> accounts) {
		if (accounts == null || accounts.isEmpty()) {
			return null;
		}
		if (PhoneNumberUtil.isNaPhoneNumber(phoneNumber)) {
			return naDialplanExecutor.internalExecute(userSipProfile,
					phoneNumber, accounts);
		} else {
			return internationalDialplanExecutor.internalExecute(
					userSipProfile, phoneNumber, accounts);
		}
	}
}
