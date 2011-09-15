/**
 * 
 */
package com.mycallstation.dataaccess.dao;

import java.util.Collection;

import com.mycallstation.base.dao.DAO;
import com.mycallstation.constant.OnlineStatus;
import com.mycallstation.dataaccess.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public interface UserSipProfileDAO extends DAO<UserSipProfile, Long> {
	public void updateOnlineStatus(OnlineStatus onlineStatus,
			UserSipProfile... userSipProfiles);

	public Collection<Long> checkAddressBindingExpires();

	public void updateLastReceiveCallTime(UserSipProfile... userSipProfiles);
}
