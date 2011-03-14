/**
 * 
 */
package com.mycallstation.base.model;

import java.io.Serializable;

/**
 * @author Jack
 * 
 */
public interface IdBasedEntity<ID extends Serializable> {
	/**
	 * @return the id
	 */
	public ID getId();
}
