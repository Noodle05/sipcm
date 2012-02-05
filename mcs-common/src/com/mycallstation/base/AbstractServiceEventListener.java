/**
 * 
 */
package com.mycallstation.base;

import java.io.Serializable;

import com.mycallstation.base.model.IdBasedEntity;

/**
 * @author Wei Gao
 * 
 */
public class AbstractServiceEventListener<Entity extends IdBasedEntity<ID>, ID extends Serializable>
		implements ServiceEventListener<Entity, ID> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.ServiceEventListener#entityCreated(com.mycallstation
	 * .base.EntityEventObject)
	 */
	@Override
	public void entityCreated(EntityEventObject<Entity, ID> event) {
		// Do nothing
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
		// Do nothing
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
		// Do nothing
	}
}
