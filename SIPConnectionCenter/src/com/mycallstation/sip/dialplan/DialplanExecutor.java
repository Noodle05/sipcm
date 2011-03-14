/**
 * 
 */
package com.mycallstation.sip.dialplan;

import com.mycallstation.sip.model.UserSipProfile;
import com.mycallstation.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public interface DialplanExecutor {
	public UserVoipAccount execute(UserSipProfile userSipProfile,
			String phoneNumber);
}
