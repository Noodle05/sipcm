/**
 * 
 */
package com.mycallstation.common.business;

import com.mycallstation.base.business.Service;
import com.mycallstation.common.model.Role;

/**
 * @author wgao
 * 
 */
public interface RoleService extends Service<Role, Integer> {
	public static final String USER_ROLE = "ROLE_USER";
	public static final String ADMIN_ROLE = "ROLE_ADMIN";
	public static final String CALLER_ROLE = "ROLE_CALLER";

	public Role getUserRole();

	public Role getCallerRole();

	public Role getAdminRole();
}
