/**
 * 
 */
package com.sipcm.common.business;

import com.sipcm.base.business.Service;
import com.sipcm.common.ActiveMethod;
import com.sipcm.common.model.User;
import com.sipcm.common.model.UserActivation;

/**
 * @author wgao
 * 
 */
public interface UserActivationService extends Service<UserActivation, Long> {
	public UserActivation createUserActivation(User owner, ActiveMethod method,
			int expireHours);

	public UserActivation getUserActivationByUser(User user);
}
