/**
 * 
 */
package com.sipcm.sip.business;

import java.util.Collection;

import com.sipcm.base.business.Service;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
public interface UserVoipAccountService extends Service<UserVoipAccount, Long> {
	public Collection<UserVoipAccount> getOutgoingAccounts(UserSipProfile user);

	public UserVoipAccount getIncomingAccount(UserSipProfile user);

	public UserVoipAccount getUserVoipAccountByVendorAndAccount(
			VoipVendor voipVender, String account);
}
