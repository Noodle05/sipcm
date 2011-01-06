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
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Component("userSipProfileServiceEventListener")
public class UserSipProfileServiceEventListener implements
		ServiceEventListener<UserSipProfile, Long> {
	@Resource(name = "sipLocationService")
	private LocationService locationService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.ServiceEventListener#entityCreated(com.sipcm.base.
	 * EntityEventObject)
	 */
	@Override
	public void entityCreated(EntityEventObject<UserSipProfile, Long> event) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.ServiceEventListener#entityModified(com.sipcm.base.
	 * EntityEventObject)
	 */
	@Override
	public void entityModified(EntityEventObject<UserSipProfile, Long> event) {
		UserSipProfile[] users = event.getSource();
		Collection<Long> changedIds = new ArrayList<Long>(users.length);
		for (UserSipProfile user : users) {
			if (user.getOwner().getStatus().isActive()) {
				changedIds.add(user.getId());
			}
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
	public void entityDeleted(EntityEventObject<UserSipProfile, Long> event) {
		UserSipProfile[] users = event.getSource();
		Long[] ids = new Long[users.length];
		for (int i = 0; i < users.length; i++) {
			ids[i] = users[i].getId();
		}
		locationService.onUserDeleted(ids);
	}

}
