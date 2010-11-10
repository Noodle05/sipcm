/**
 * 
 */
package com.sipcm.sip.dialplan;

import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public interface DialplanExecutor {
	public UserVoipAccount execute(UserSipProfile userSipProfile,
			String phoneNumber);
}
