/**
 * 
 */
package com.mycallstation.sip.business;

import java.util.Collection;

import com.mycallstation.base.business.Service;
import com.mycallstation.common.OnlineStatus;
import com.mycallstation.common.model.User;
import com.mycallstation.sip.model.UserSipProfile;

/**
 * @author wgao
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
}
