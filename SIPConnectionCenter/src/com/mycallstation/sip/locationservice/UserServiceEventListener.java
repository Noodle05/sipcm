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
import com.mycallstation.common.model.User;

/**
 * @author wgao
 * 
 */
@Component("locationServiceUserEventListener")
public class UserServiceEventListener extends
		AbstractServiceEventListener<User, Long> {
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
	public void entityModified(EntityEventObject<User, Long> event) {
		Collection<User> users = event.getSource();
		Collection<Long> changedIds = new ArrayList<Long>(users.size());
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
			locationService.onUserChanged(ids);
		}
	}
}
