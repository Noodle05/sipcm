/**
 * 
 */
package com.sipcm.sip.business;

import com.sipcm.base.business.Service;
import com.sipcm.common.OnlineStatus;
import com.sipcm.common.model.User;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public interface UserSipProfileService extends Service<UserSipProfile, Long> {
	public UserSipProfile createUserSipProfile(User user);

	public UserSipProfile getUserSipProfileByUsername(String username);

	public UserSipProfile getUserSipProfileByPhoneNumber(String phoneNumber);

	public void updateOnlineStatus(OnlineStatus onlineStatusm,
			UserSipProfile... userSipProfiles);
}
