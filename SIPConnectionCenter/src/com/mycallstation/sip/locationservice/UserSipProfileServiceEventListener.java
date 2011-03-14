/**
 * 
 */
package com.mycallstation.sip.locationservice;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mycallstation.base.AbstractServiceEventListener;
import com.mycallstation.base.EntityEventObject;
import com.mycallstation.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@Component("userSipProfileServiceEventListener")
public class UserSipProfileServiceEventListener extends
		AbstractServiceEventListener<UserSipProfile, Long> {
	@Resource(name = "sipLocationService")
	private LocationService locationService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.ServiceEventListener#entityModified(com.mycallstation
	 * .base. EntityEventObject)
	 */
	@Override
	public void entityModified(EntityEventObject<UserSipProfile, Long> event) {
		Collection<UserSipProfile> users = event.getSource();
		Collection<Long> changedIds = new ArrayList<Long>(users.size());
		for (UserSipProfile user : users) {
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
	 * @see
	 * com.mycallstation.base.AbstractServiceEventListener#entityDeleted(com
	 * .mycallstation.base .EntityEventObject)
	 */
	@Override
	public void entityDeleted(EntityEventObject<UserSipProfile, Long> event) {
		Collection<UserSipProfile> users = event.getSource();
		Collection<Long> deletedIds = new ArrayList<Long>(users.size());
		for (UserSipProfile user : users) {
			deletedIds.add(user.getId());
		}
		if (!deletedIds.isEmpty()) {
			Long[] ids = new Long[deletedIds.size()];
			ids = deletedIds.toArray(ids);
			locationService.onUserChanged(ids);
		}
	}
}
