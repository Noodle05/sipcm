/**
 * 
 */
package com.mycallstation.sip.dao;

import java.util.Collection;

import com.mycallstation.base.dao.DAO;
import com.mycallstation.common.OnlineStatus;
import com.mycallstation.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public interface UserSipProfileDAO extends DAO<UserSipProfile, Long> {
	public void updateOnlineStatus(OnlineStatus onlineStatus,
			UserSipProfile... userSipProfiles);

	public Collection<Long> checkAddressBindingExpires();
}
