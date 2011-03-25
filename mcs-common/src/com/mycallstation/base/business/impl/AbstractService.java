/**
 * 
 */
package com.mycallstation.base.business.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mycallstation.base.business.Service;
import com.mycallstation.base.dao.DAO;
import com.mycallstation.base.filter.FSP;
import com.mycallstation.base.filter.Filter;
import com.mycallstation.base.filter.FilterFactory;

/**
 * @author Jack
 */
@Transactional(readOnly = true)
public abstract class AbstractService<Entity extends Serializable, ID extends Serializable>
		implements Service<Entity, ID> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected DAO<Entity, ID> dao;

	protected Class<Entity> entityClass;

	@Resource(name = "filterFactory")
	protected FilterFactory filterFactory;

	/**
	 * Initialize service object.
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		entityClass = (Class<Entity>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		if (logger.isDebugEnabled()) {
			logger.debug("Entity class been initialized as: {}",
					entityClass.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.business.Service#createNewEntity()
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Entity createNewEntity() {
		if (logger.isTraceEnabled()) {
			logger.trace("Creating new entity instance.");
		}
		try {
			return entityClass.newInstance();
		} catch (InstantiationException e) {
			throw new BeanInstantiationException(entityClass, "", e);
		} catch (IllegalAccessException e) {
			throw new BeanInstantiationException(entityClass, "", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#getEntityById(java.io.Serializable
	 * )
	 */
	@Override
	public Entity getEntityById(ID id) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting entity by id: {}", id);
		}
		return dao.getEntityById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#getEntities(com.mycallstation
	 * .base.filter.FSP)
	 */
	@Override
	public List<Entity> getEntities(FSP fsp) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting entities by fsp: {}", fsp);
		}
		return dao.getEntities(fsp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#getEntities(com.mycallstation
	 * .base.filter.Filter)
	 */
	@Override
	public List<Entity> getEntities(Filter filter) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting entities by filter: {}", filter);
		}
		return dao.getEntities(filter, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.business.Service#getEntities()
	 */
	@Override
	public List<Entity> getEntities() {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting all entities");
		}
		return dao.getEntities(null, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#getUniqueEntity(com.mycallstation
	 * .base.filter.Filter)
	 */
	@Override
	public Entity getUniqueEntity(Filter filter) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting unique entity by filter: {}", filter);
		}
		return dao.getUniqueEntity(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#getRowCount(com.mycallstation
	 * .base.filter.Filter)
	 */
	@Override
	public int getRowCount(Filter filter) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting row count by filter: {}", filter);
		}
		return dao.getRowCount(filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.business.Service#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting row count");
		}
		return dao.getRowCount(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#isReadonly(java.io.Serializable)
	 */
	@Override
	public boolean isReadonly(Entity entity) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#refreshEntity(java.io.Serializable
	 * )
	 */
	@Override
	public Entity refreshEntity(Entity entity) {
		if (logger.isTraceEnabled()) {
			logger.trace("Refreshing entity: \"{}\"", entity);
		}
		return dao.refreshEntity(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#removeEntity(java.io.Serializable
	 * )
	 */
	@Override
	@Transactional(readOnly = false)
	public Entity removeEntity(Entity entity) {
		if (logger.isTraceEnabled()) {
			logger.trace("Removing entity: \"{}\"", entity);
		}
		return dao.removeEntity(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#removeEntities(java.util.Collection
	 * )
	 */
	@Override
	@Transactional(readOnly = false)
	public Collection<Entity> removeEntities(Collection<Entity> entities) {
		if (logger.isTraceEnabled()) {
			logger.trace("Removing entities, total number of entities: {}",
					(entities == null ? 0 : entities.size()));
		}
		return dao.removeEntities(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#removeEntityById(java.io.Serializable
	 * )
	 */
	@Override
	@Transactional(readOnly = false)
	public Entity removeEntityById(ID id) {
		if (logger.isTraceEnabled()) {
			logger.trace("Removing entity by id: \"{}\"", id);
		}
		return dao.removeEntityById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#saveEntity(java.io.Serializable)
	 */
	@Override
	@Transactional(readOnly = false)
	public Entity saveEntity(Entity entity) {
		if (logger.isTraceEnabled()) {
			logger.trace("Saving entity: \"{}\"", entity);
		}
		return dao.saveEntity(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#saveEntities(java.util.Collection
	 * )
	 */
	@Override
	@Transactional(readOnly = false)
	public Collection<Entity> saveEntities(Collection<Entity> entities) {
		if (logger.isTraceEnabled()) {
			logger.trace("Saving entities, total number of entities: {}",
					(entities == null ? 0 : entities.size()));
		}
		return dao.saveEntities(entities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.base.business.Service#getEntityId(java.io.Serializable)
	 */
	@Override
	public ID getEntityId(Entity entity) {
		if (logger.isTraceEnabled()) {
			logger.trace("Getting id of entity: \"{}\"", entity);
		}
		return dao.getEntityId(entity);
	}

	/**
	 * Set dao object.
	 * 
	 * @param dao
	 */
	public abstract void setDAO(DAO<Entity, ID> dao);
}
