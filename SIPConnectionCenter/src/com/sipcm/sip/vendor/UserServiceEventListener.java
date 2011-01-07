/**
 * 
 */
package com.sipcm.sip.vendor;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.base.AbstractServiceEventListener;
import com.sipcm.base.EntityEventObject;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@Component("vendorManagerUserEventListener")
public class UserServiceEventListener extends
		AbstractServiceEventListener<User, Long> {
	@Resource(name = "voipVendorManager")
	private VoipVendorManager voipVendorManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.ServiceEventListener#entityDeleted(com.sipcm.base.
	 * EntityEventObject)
	 */
	@Override
	public void entityDeleted(EntityEventObject<User, Long> event) {
		User[] users = event.getSource();
		Long[] ids = new Long[users.length];
		for (int i = 0; i < users.length; i++) {
			ids[i] = users[i].getId();
		}
		voipVendorManager.onUserDeleted(ids);
	}
}
