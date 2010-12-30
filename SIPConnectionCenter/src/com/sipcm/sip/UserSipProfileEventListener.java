/**
 * 
 */
package com.sipcm.sip;

import com.sipcm.base.EntityEventObject;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public interface UserSipProfileEventListener {
	public void userModified(EntityEventObject<UserSipProfile, Long> event);

	public void userDeleted(EntityEventObject<UserSipProfile, Long> event);
}
