/**
 * 
 */
package com.mycallstation.events;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mycallstation.base.AbstractServiceEventListener;
import com.mycallstation.base.EntityEventObject;
import com.mycallstation.communication.UserEventMessageSender;
import com.mycallstation.dataaccess.model.User;

/**
 * @author Wei Gao
 * 
 */
@Component("userServiceEventListener")
public class UserServiceEventListener extends
		AbstractServiceEventListener<User, Long> {
	@Resource(name = "userEventMessageSender")
	private UserEventMessageSender userEventMessageSender;

	@Override
	public void entityCreated(EntityEventObject<User, Long> event) {
		Collection<User> users = event.getSource();
		Collection<Long> changedIds = new ArrayList<Long>(users.size());
		for (User user : users) {
			changedIds.add(user.getId());
		}
		if (!changedIds.isEmpty()) {
			Long[] ids = new Long[changedIds.size()];
			ids = changedIds.toArray(ids);
			userEventMessageSender.sendUserCreatedEvent(ids);
		}
	}

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
			userEventMessageSender.sendUserModifiedEvent(ids);
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
			userEventMessageSender.sendUserDeletedEvent(ids);
		}
	}
}
