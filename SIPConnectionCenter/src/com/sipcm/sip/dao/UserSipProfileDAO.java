/**
 * 
 */
package com.sipcm.sip.dao;

import com.sipcm.base.dao.DAO;
import com.sipcm.common.OnlineStatus;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public interface UserSipProfileDAO extends DAO<UserSipProfile, Long> {
	public void updateOnlineStatus(OnlineStatus onlineStatus,
			UserSipProfile... userSipProfiles);
}
