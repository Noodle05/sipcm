/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.base.EntityEventObject;
import com.sipcm.base.ServiceEventListener;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@Component("userServiceEventListener")
public class UserServiceEventListener implements
		ServiceEventListener<User, Long> {
	@Resource(name = "sipLocationService")
	private LocationService locationService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.ServiceEventListener#entityCreated(com.sipcm.base.
	 * EntityEventObject)
	 */
	@Override
	public void entityCreated(EntityEventObject<User, Long> event) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.ServiceEventListener#entityModified(com.sipcm.base.
	 * EntityEventObject)
	 */
	@Override
	public void entityModified(EntityEventObject<User, Long> event) {
		User[] users = event.getSource();
		Collection<Long> changedIds = new ArrayList<Long>(users.length);
		for (User user : users) {
			changedIds.add(user.getId());
		}
		if (!changedIds.isEmpty()) {
			Long[] ids = new Long[changedIds.size()];
			ids = changedIds.toArray(ids);
			locationService.onUserChanged(ids);
		}
	}

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
		locationService.onUserDeleted(ids);
	}

}
