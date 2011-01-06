/**
 * 
 */
package com.sipcm.sip.business;

import java.util.Collection;

import com.sipcm.base.business.Service;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public interface UserVoipAccountService extends Service<UserVoipAccount, Long> {
	public Collection<UserVoipAccount> getOutgoingAccounts(UserSipProfile user);
}
