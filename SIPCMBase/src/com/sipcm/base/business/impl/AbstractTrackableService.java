/**
 * 
 */
package com.sipcm.base.business.impl;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sipcm.base.model.TrackableEntity;

/**
 * @author wgao
 * 
 */
@Transactional(readOnly = true)
public abstract class AbstractTrackableService<Entity extends TrackableEntity, ID extends Serializable>
		extends AbstractService<Entity, ID> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.business.impl.AbstractService#createNewEntity()
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Entity createNewEntity() {
		Entity entity = super.createNewEntity();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		entity.setCreateDate(now);
		entity.setLastModify(now);
		return entity;
	}
}
