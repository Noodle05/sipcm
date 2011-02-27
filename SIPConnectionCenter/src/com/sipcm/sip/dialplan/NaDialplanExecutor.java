/**
 * 
 */
package com.sipcm.sip.dialplan;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
@Component("sipNaDialplanExecutor")
public class NaDialplanExecutor extends AbstractDialplanExecutor {
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
