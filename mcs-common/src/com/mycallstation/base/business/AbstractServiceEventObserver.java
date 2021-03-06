/**
 * 
 */
package com.mycallstation.base.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.base.EntityEventObject;
import com.mycallstation.base.ServiceEventListener;
import com.mycallstation.base.model.IdBasedEntity;

/**
 * @author Wei Gao
 * 
 */
public abstract class AbstractServiceEventObserver<Entity extends IdBasedEntity<ID>, ID extends Serializable> {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ServiceEventListener<Entity, ID> listener;

	protected void setListener(ServiceEventListener<Entity, ID> listener) {
		this.listener = listener;
	}

	protected final Object aroundSaveEntity(ProceedingJoinPoint pjp,
			Entity entity) throws Throwable {
		if (listener == null) {
			return pjp.proceed(new Object[] { entity });
		}
		boolean isNew = true;
		if (entity != null) {
			if (entity.getId() != null) {
				isNew = false;
			}
		}
		Object ret = pjp.proceed(new Object[] { entity });
		if (logger.isDebugEnabled()) {
			logger.debug("Entity: \"{}\" been saved, notify listener.", entity);
		}
		try {
			Collection<Entity> entities = new ArrayList<Entity>(1);
			entities.add(entity);
			EntityEventObject<Entity, ID> event = new EntityEventObject<Entity, ID>(
					entities);
			if (isNew) {
				listener.entityCreated(event);
			} else {
				listener.entityModified(event);
			}
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error happened when notify listener..", e);
			}
		}
		return ret;
	}

	protected final Object aroundSaveEntities(ProceedingJoinPoint pjp,
			Collection<Entity> entities) throws Throwable {
		if (listener == null) {
			return pjp.proceed(new Object[] { entities });
		}
		Collection<Entity> newEntities;
		Collection<Entity> existingEntities;
		if (entities != null && !entities.isEmpty()) {
			newEntities = new ArrayList<Entity>();
			existingEntities = new ArrayList<Entity>();
			for (Entity entity : entities) {
				if (entity.getId() == null) {
					newEntities.add(entity);
				} else {
					existingEntities.add(entity);
				}
			}
		} else {
			newEntities = Collections.emptyList();
			existingEntities = Collections.emptyList();
		}
		Object ret = pjp.proceed(new Object[] { entities });
		if (!newEntities.isEmpty() || !existingEntities.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Collection of entities saved, notify listener.");
			}
			if (!newEntities.isEmpty()) {
				EntityEventObject<Entity, ID> event = new EntityEventObject<Entity, ID>(
						newEntities);
				try {
					listener.entityCreated(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error("Error happened when notify listener..", e);
					}
				}
			}
			if (!existingEntities.isEmpty()) {
				EntityEventObject<Entity, ID> event = new EntityEventObject<Entity, ID>(
						existingEntities);
				try {
					listener.entityModified(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error("Error happened when notify listener..", e);
					}
				}
			}
		}
		return ret;
	}

	protected final void afterDeleteEntity(Entity entity) {
		if (listener == null) {
			return;
		}
		if (entity != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Entity \"{}\" been deleted, notify listener.",
						entity);
			}
			try {
				Collection<Entity> entities = new ArrayList<Entity>(1);
				entities.add(entity);
				EntityEventObject<Entity, ID> event = new EntityEventObject<Entity, ID>(
						entities);
				listener.entityDeleted(event);
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error happened when notify listener.", e);
				}
			}
		}
	}

	protected final void afterDeleteEntities(Collection<Entity> entities) {
		if (listener == null) {
			return;
		}
		if (entities != null && !entities.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Collection of entities been deleted, notify listener.");
			}
			EntityEventObject<Entity, ID> event = new EntityEventObject<Entity, ID>(
					entities);
			try {
				listener.entityDeleted(event);
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error happened when notify listener..", e);
				}
			}
		}
	}
}
