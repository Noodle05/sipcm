/**
 * 
 */
package com.mycallstation.base.model;

import java.io.Serializable;

/**
 * @author Wei Gao
 * 
 */
public interface IdBasedEntity<ID extends Serializable> {
	/**
	 * @return the id
	 */
	public ID getId();
}
