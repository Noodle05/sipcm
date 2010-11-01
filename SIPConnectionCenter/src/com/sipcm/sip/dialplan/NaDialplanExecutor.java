/**
 * 
 */
package com.sipcm.sip.dialplan;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.sipcm.common.model.User;
import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
@Component("naDialplanExecutor")
public class NaDialplanExecutor extends AbstractDialplanExecutor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.dialplan.DialplanExecutor#execute(com.sipcm.common.model
	 * .User, java.lang.String)
	 */
	@Override
	public UserVoipAccount execute(User user, String phoneNumber) {
		Set<UserVoipAccount> accounts = user.getVoipAccounts();
		if (accounts.size() == 1) {
			return accounts.iterator().next();
		} else {
			UserVoipAccount account = null;
			for (UserVoipAccount a : accounts) {
				if (account == null) {
					account = a;
				}
				if (VoipVendorType.GOOGLE_VOICE.equals(a.getVoipVendor()
						.getType())) {
					account = a;
					break;
				}
			}
			return account;
		}
	}
}
