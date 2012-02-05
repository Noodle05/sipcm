/**
 * 
 */
package com.mycallstation.dataaccess.business;

import java.util.Collection;

import com.mycallstation.base.business.Service;
import com.mycallstation.constant.OnlineStatus;
import com.mycallstation.dataaccess.model.User;
import com.mycallstation.dataaccess.model.UserSipProfile;

/**
 * @author Wei Gao
 * 
 */
public interface UserSipProfileService extends Service<UserSipProfile, Long> {
	public UserSipProfile createUserSipProfile(User user);

	public UserSipProfile getUserSipProfileByUser(User user);

	public UserSipProfile getUserSipProfileByUsername(String username);

	public UserSipProfile getUserSipProfileByVerifiedPhoneNumber(
			String phoneNumber);

	public void updateOnlineStatus(OnlineStatus onlineStatusm,
			UserSipProfile... userSipProfiles);

	public Collection<Long> checkAddressBindingExpires();

	public void updateLastReceiveCallTime(UserSipProfile... userSipProfiles);

	public Collection<Long> getNeedPingUserSipProfile(int timeout,
			boolean onlineOnly);
}
