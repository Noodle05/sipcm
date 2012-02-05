/**
 * 
 */
package com.mycallstation.sip.dialplan;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.mycallstation.constant.VoipVendorType;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * @author Wei Gao
 * 
 */
@Component("sipInternationalDialplanExecutor")
public class InternationalDialplanExecutor extends AbstractDialplanExecutor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.dialplan.DialplanExecutor#execute(com.mycallstation
	 * .sip.model. UserSipProfile, java.lang.String)
	 */
	@Override
	public UserVoipAccount internalExecute(UserSipProfile userSipProfile,
			String phoneNumber, Collection<UserVoipAccount> accounts) {
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
