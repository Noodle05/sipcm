/**
 * 
 */
package com.sipcm.common.business;

import com.sipcm.base.business.Service;
import com.sipcm.common.model.User;
import com.sipcm.common.model.UserActivation;

/**
 * @author wgao
 * 
 */
public interface UserActivationService extends Service<UserActivation, Long> {
	public UserActivation createUserActivation(User owner);
}
