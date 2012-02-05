/**
 * 
 */
package com.mycallstation.base;

import java.io.Serializable;
import java.util.Collection;

import com.mycallstation.base.model.IdBasedEntity;

/**
 * @author Wei Gao
 * 
 */
public class CompositeServiceEventListener<Entity extends IdBasedEntity<ID>, ID extends Serializable>
		implements ServiceEventListener<Entity, ID> {
	private Collection<ServiceEventListener<Entity, ID>> listeners;

	/**
	 * @param listeners
	 *            the listeners to set
	 */
	public void setListeners(
			Collection<ServiceEventListener<Entity, ID>> listeners) {
		this.listeners = listeners;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.ServiceEventListener#entityCreated(com.mycallstation
	 * .base.EntityEventObject)
	 */
	@Override
	public void entityCreated(EntityEventObject<Entity, ID> event) {
		if (listeners != null) {
			for (ServiceEventListener<Entity, ID> listener : listeners) {
				listener.entityCreated(event);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.ServiceEventListener#entityModified(com.mycallstation
	 * .base.EntityEventObject)
	 */
	@Override
	public void entityModified(EntityEventObject<Entity, ID> event) {
		if (listeners != null) {
			for (ServiceEventListener<Entity, ID> listener : listeners) {
				listener.entityModified(event);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.ServiceEventListener#entityDeleted(com.mycallstation
	 * .base.EntityEventObject)
	 */
	@Override
	public void entityDeleted(EntityEventObject<Entity, ID> event) {
		if (listeners != null) {
			for (ServiceEventListener<Entity, ID> listener : listeners) {
				listener.entityDeleted(event);
			}
		}
	}
}
