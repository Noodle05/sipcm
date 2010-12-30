/**
 * 
 */
package com.sipcm.base;

import java.io.Serializable;
import java.util.EventObject;

import com.sipcm.base.model.IdBasedEntity;

/**
 * @author wgao
 * 
 */
public class EntityEventObject<Entity extends IdBasedEntity<ID>, ID extends Serializable>
		extends EventObject {
	private static final long serialVersionUID = 78096042020821625L;

	public EntityEventObject(Entity... entity) {
		super(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#getSource()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Entity[] getSource() {
		return (Entity[]) source;
	}
}
