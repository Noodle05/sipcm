/**
 * 
 */
package com.mycallstation.base;

import java.io.Serializable;
import java.util.EventListener;

import com.mycallstation.base.model.IdBasedEntity;

/**
 * @author Wei Gao
 * 
 */
public interface ServiceEventListener<Entity extends IdBasedEntity<ID>, ID extends Serializable>
		extends EventListener {
	public void entityCreated(EntityEventObject<Entity, ID> event);

	public void entityModified(EntityEventObject<Entity, ID> event);

	public void entityDeleted(EntityEventObject<Entity, ID> event);
}
