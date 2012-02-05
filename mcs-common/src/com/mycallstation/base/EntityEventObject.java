/**
 * 
 */
package com.mycallstation.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.EventObject;

import com.mycallstation.base.model.IdBasedEntity;

/**
 * @author Wei Gao
 * 
 */
public class EntityEventObject<Entity extends IdBasedEntity<ID>, ID extends Serializable>
		extends EventObject {
	private static final long serialVersionUID = 78096042020821625L;

	public EntityEventObject(Collection<Entity> entity) {
		super(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#getSource()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Entity> getSource() {
		return (Collection<Entity>) source;
	}
}
