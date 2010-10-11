/**
 * 
 */
package com.sipcm.common.business;

import com.sipcm.base.business.Service;
import com.sipcm.common.model.User;

/**
 * @author Jack
 * 
 */
public interface UserService extends Service<User, Long> {
	public User getUserByUsername(String username);

	public User getUserBySipId(String sipId);

	public User setPassword(User entity, String password);

	public boolean matchPassword(User entity, String password);
}
