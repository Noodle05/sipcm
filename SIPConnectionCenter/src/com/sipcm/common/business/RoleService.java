/**
 * 
 */
package com.sipcm.common.business;

import com.sipcm.base.business.Service;
import com.sipcm.common.model.Role;

/**
 * @author wgao
 * 
 */
public interface RoleService extends Service<Role, Integer> {
	public static final String CALLER_ROLE = "caller";
	public static final String ADMIN_ROLE = "admin";

	public Role getCallerRole();
}
