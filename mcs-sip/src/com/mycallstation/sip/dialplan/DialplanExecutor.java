/**
 * 
 */
package com.mycallstation.sip.dialplan;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * @author Wei Gao
 * 
 */
public interface DialplanExecutor {
	public UserVoipAccount execute(UserSipProfile userSipProfile,
			String phoneNumber);
}
