/**
 * 
 */
package com.mycallstation.dataaccess.dao;

import java.util.Collection;

import com.mycallstation.base.dao.DAO;
import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public interface UserVoipAccountDAO extends DAO<UserVoipAccount, Long> {
	public void updateRegisterExpires(UserVoipAccount account);

	public void updateAuthResponse(UserVoipAccount account);

	public void updateRegisterExpiresAndAuthResponse(UserVoipAccount account);

	public Collection<Long> checkRegisterExpires(int minExpires);
}
