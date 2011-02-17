/**
 * 
 */
package com.sipcm.sip.dao;

import com.sipcm.base.dao.DAO;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public interface UserVoipAccountDAO extends DAO<UserVoipAccount, Long> {
	public void updateOnlineStatus(UserVoipAccount account);
}
