/**
 * 
 */
package com.sipcm.sip.dialplan;

import com.sipcm.common.model.User;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public interface DialplanExecutor {
	public UserVoipAccount execute(User user, String phoneNumber);
}
