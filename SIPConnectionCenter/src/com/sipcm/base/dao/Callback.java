/**
 * 
 */
package com.sipcm.base.dao;

import java.io.Serializable;

/**
 * @author wgao
 * 
 */
public interface Callback<Entity extends Serializable, ID extends Serializable> {
	public void execute(DAO<Entity, ID> dao, Entity entity);
}
