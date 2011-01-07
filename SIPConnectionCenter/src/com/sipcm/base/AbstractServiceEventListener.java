/**
 * 
 */
package com.sipcm.base;

import java.io.Serializable;

import com.sipcm.base.model.IdBasedEntity;

/**
 * @author wgao
 * 
 */
public class AbstractServiceEventListener<Entity extends IdBasedEntity<ID>, ID extends Serializable>
		implements ServiceEventListener<Entity, ID> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.ServiceEventListener#entityCreated(com.sipcm.base.
	 * EntityEventObject)
	 */
	@Override
	public void entityCreated(EntityEventObject<Entity, ID> event) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.ServiceEventListener#entityModified(com.sipcm.base.
	 * EntityEventObject)
	 */
	@Override
	public void entityModified(EntityEventObject<Entity, ID> event) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.ServiceEventListener#entityDeleted(com.sipcm.base.
	 * EntityEventObject)
	 */
	@Override
	public void entityDeleted(EntityEventObject<Entity, ID> event) {
		// Do nothing
	}
}
