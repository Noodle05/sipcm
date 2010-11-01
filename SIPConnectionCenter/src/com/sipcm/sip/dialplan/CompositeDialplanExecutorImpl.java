/**
 * 
 */
package com.sipcm.sip.dialplan;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.common.model.User;
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
	 * @see
	 * com.sipcm.sip.dialplan.DialplanExcutor#excute(com.sipcm.common.model.
	 * User, java.lang.String)
	 */
	@Override
	public UserVoipAccount execute(User user, String phoneNumber) {
		Set<UserVoipAccount> accounts = user.getVoipAccounts();
		if (accounts == null || accounts.isEmpty()) {
			return null;
		}
		if (phoneNumberUtil.isNaPhoneNumber(phoneNumber)) {
			return naDialplanExecutor.execute(user, phoneNumber);
		} else {
			return internationalDialplanExecutor.execute(user, phoneNumber);
		}
	}
}
