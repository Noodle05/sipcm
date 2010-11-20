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
	public static final String callerRole = "caller";

	public Role getCallerRole();
}
