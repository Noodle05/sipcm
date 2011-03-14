/**
 * 
 */
package com.mycallstation.base.business;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.mycallstation.base.filter.FSP;
import com.mycallstation.base.filter.Filter;

/**
 * @author Jack
 * 
 */
public interface Service<Entity extends Serializable, ID extends Serializable> {
	/**
	 * Create a new instance of entity.
	 * 
	 * @return entity
	 */
	public Entity createNewEntity();

	/**
	 * Generic method used to get list of entities based on FSP (Filter, Sorting
	 * and Pagingnation).
	 * 
	 * @param fsp
	 *            Filter, Sorting and Pagination
	 * @return list of populated entities
	 */
	public List<Entity> getEntities(FSP fsp);

	/**
	 * Generic method used to get list of entities based on filter.
	 * 
	 * @param filter
	 *            Filter
	 * @return list of populated entities
	 */
	public List<Entity> getEntities(Filter filter);

	/**
	 * Generic method used to get all entities of a particular type.
	 * 
	 * @return List of populated entities
	 */
	public List<Entity> getEntities();

	/**
	 * Generic method used to single entity of a particular type based on
	 * filter.
	 * 
	 * @param filter
	 *            Filter
	 * @return Single entity
	 */
	public Entity getUniqueEntity(Filter filter);

	/**
	 * Get count of records based on filter.
	 * 
	 * @param filter
	 * @return number of records
	 */
	public int getRowCount(Filter filter);

	/**
	 * Get count of all records
	 * 
	 * @return number of records
	 */
	public int getRowCount();

	/**
	 * Generic method to get an entity based on class and identifier.
	 * 
	 * @param id
	 *            the identifier (primary key) of the class
	 * @return a populated entity
	 * @see org.springframework.orm.ObjectRetrievalFailureException
	 */
	public Entity getEntityById(ID id);

	/**
	 * Save an entity.
	 * 
	 * @param entity
	 * @return saved entity
	 */
	public Entity saveEntity(Entity entity);

	/**
	 * Generic method to save collection of entities.
	 * 
	 * @param entities
	 *            the entities to save
	 * @return saved entities
	 */
	public Collection<Entity> saveEntities(Collection<Entity> entities);

	/**
	 * Generic method to delete an entity based id
	 * 
	 * @param id
	 *            the identifier of the class
	 * @return removed entity
	 */
	public Entity removeEntityById(ID id);

	/**
	 * Generic method to delete an entity
	 * 
	 * @param entity
	 *            the entity to remove
	 * @return removed entity
	 */
	public Entity removeEntity(Entity entity);

	/**
	 * Delete entities
	 * 
	 * @param entities
	 * @return removed entities
	 */
	public Collection<Entity> removeEntities(Collection<Entity> entities);

	/**
	 * Get entity id
	 * 
	 * @param entity
	 * @return id
	 */
	public ID getEntityId(Entity entity);

	/**
	 * Generic method to refresh an entity
	 * 
	 * @param entity
	 *            the entity to refresh.
	 * @return refreshed entity
	 */
	public Entity refreshEntity(Entity entity);

	/**
	 * Check if the entity is read only
	 * 
	 * @param entity
	 *            the entity to check
	 * @return true if the entity is read only, otherwise false.
	 */
	public boolean isReadonly(Entity entity);
}
