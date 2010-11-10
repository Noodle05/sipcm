/**
 * 
 */
package com.sipcm.sip.dialplan;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
@Component("internationalDialplanExecutor")
public class InternationalDialplanExecutor extends AbstractDialplanExecutor {
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
		if (accounts.size() == 1) {
			return accounts.iterator().next();
		} else {
			UserVoipAccount account = null;
			for (UserVoipAccount a : accounts) {
				if (account == null) {
					account = a;
				}
				if (VoipVendorType.SIP.equals(a.getVoipVendor().getType())) {
					account = a;
					break;
				}
			}
			return account;
		}
	}

}
