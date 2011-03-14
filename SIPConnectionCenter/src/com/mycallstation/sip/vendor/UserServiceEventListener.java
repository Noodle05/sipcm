/**
 * 
 */
package com.mycallstation.sip.vendor;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mycallstation.base.AbstractServiceEventListener;
import com.mycallstation.base.EntityEventObject;
import com.mycallstation.common.model.User;

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
	 * @see
	 * com.mycallstation.base.ServiceEventListener#entityDeleted(com.mycallstation
	 * .base. EntityEventObject)
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
