/**
 * 
 */
package com.mycallstation.dataaccess.business;

import com.mycallstation.base.business.Service;
import com.mycallstation.constant.ActiveMethod;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserActivation;

/**
 * @author Wei Gao
 * 
 */
public interface UserActivationService extends Service<UserActivation, Long> {
	public UserActivation createUserActivation(User owner, ActiveMethod method,
			int expireHours);

	public UserActivation getUserActivationByUser(User user);

	public UserActivation updateExpires(UserActivation userActivation,
			int expireHours);
}
