/**
 * 
 */
package com.mycallstation.sip.dialplan;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public interface DialplanExecutor {
	public UserVoipAccount execute(UserSipProfile userSipProfile,
			String phoneNumber);
}
