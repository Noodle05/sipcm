/**
 * 
 */
package com.mycallstation.common.business;

import com.mycallstation.base.business.Service;
import com.mycallstation.common.ActiveMethod;
import com.mycallstation.common.model.User;
import com.mycallstation.common.model.UserActivation;

/**
 * @author wgao
 * 
 */
public interface UserActivationService extends Service<UserActivation, Long> {
	public UserActivation createUserActivation(User owner, ActiveMethod method,
			int expireHours);

	public UserActivation getUserActivationByUser(User user);

	public UserActivation updateExpires(UserActivation userActivation,
			int expireHours);
}
