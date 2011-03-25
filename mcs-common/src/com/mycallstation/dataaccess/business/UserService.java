/**
 * 
 */
package com.mycallstation.dataaccess.business;

import com.mycallstation.base.business.Service;
import com.mycallstation.dataaccess.model.User;

/**
 * @author Jack
 * 
 */
public interface UserService extends Service<User, Long> {
	public static final String REALM_NAME = "sip.server.realm";

	public User getUserByUsername(String username);

	public User setPassword(User entity, String password);

	public boolean matchPassword(User entity, String password);

	public User getUserByEmail(String email);

	public User fullyLoadUser(Long id);
}
