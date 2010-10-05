/**
 * 
 */
package com.sipcm.base.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.sipcm.base.filter.FSP;
import com.sipcm.base.filter.Filter;
import com.sipcm.base.filter.Page;
import com.sipcm.base.filter.Sort;

/**
 * @author Jack
 * 
 */
public interface DAO<Entity extends Serializable, ID extends Serializable> {
	/**
	 * Generic method used to get entities list of a particular type by filter.
	 * 
	 * @param filter
	 *            filter object used to query entities.
	 * @return list of populated entities
	 */
	public List<Entity> getEntities(Filter filter, Sort sort, Page page);

	/**
	 * Generic method used to get entities list of a particular type by FSP.
	 * 
	 * @param fsp
	 *            FSP object used to query entities.
	 * @return list of entities
	 */
	public List<Entity> getEntities(FSP fsp);

	/**
	 * Generic method used to get all entities list of a particular type.
	 * 
	 * @return list of entities
	 */
	public List<Entity> getEntities();

	/**
	 * Generic method used to get unique entity of a particular type. Be careful
	 * with this method. If filter can not unique a entity (multiple object
	 * match the filter) throw exception.
	 * 
	 * @param filter
	 *            Filter object.
	 * @return
	 */
	public Entity getUniqueEntity(Filter filter);

	/**
	 * Generic method to get an entity based on class and identifier.
	 * 
	 * @param id
	 *            the identifier (primary key) of the class
	 * @return a populated object
	 */
	public Entity getEntityById(ID id);

	/**
	 * Generic method to save an object - handles both update and insert.
	 * 
	 * @param entity
	 *            the object to save
	 * @return saved object
	 */
	public Entity saveEntity(Entity entity);

	/**
	 * Get number of records only by filter.
	 * 
	 * @param filter
	 * @return number of records
	 */
	public int getRowCount(Filter filter);

	/**
	 * Save collection of objects.
	 * 
	 * @param entities
	 * @return saved objects
	 */
	public Collection<Entity> saveEntities(Collection<Entity> entities);

	/**
	 * Generic method to delete an object based on class and id
	 * 
	 * @param id
	 *            the identifier (primary key) of the class
	 * @return removed object
	 */
	public Entity removeEntityById(ID id);

	/**
	 * Generic method to delete an object
	 * 
	 * @param entity
	 *            the object to remove
	 * @return removed object
	 */
	public Entity removeEntity(Entity entity);

	/**
	 * Remove objects
	 * 
	 * @param entities
	 * @return removed objects
	 */
	public Collection<Entity> removeEntities(Collection<Entity> entities);

	/**
	 * Get identifer of object.
	 * 
	 * @param entity
	 * @return id of object
	 */
	public ID getEntityId(Entity entity);

	/**
	 * Check if an object is not exist in datasource.
	 * 
	 * @param entity
	 * @return
	 */
	public boolean isNewEntity(Entity entity);

	/**
	 * Refresh object from datasource.
	 * 
	 * @param entity
	 * @return refreshed entity
	 */
	public Entity refreshEntity(Entity entity);
}
