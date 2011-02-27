/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.ArrayList;
import java.util.Collection;

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
		Collection<User> users = event.getSource();
		Collection<Long> deletedIds = new ArrayList<Long>(users.size());
		for (User user : users) {
			deletedIds.add(user.getId());
		}
		if (!deletedIds.isEmpty()) {
			Long[] ids = new Long[deletedIds.size()];
			ids = deletedIds.toArray(ids);
			voipVendorManager.onUserDeleted(ids);
		}
	}
}
