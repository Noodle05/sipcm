/**
 * 
 */
package com.mycallstation.base.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author wgao
 * 
 */
public interface TrackableEntity extends Serializable {
	/**
	 * @param createDate
	 *            the createDate to set
	 */
	public void setCreateDate(Timestamp createDate);

	/**
	 * @return the createDate
	 */
	public Timestamp getCreateDate();

	/**
	 * @param lastModify
	 *            the lastModify to set
	 */
	public void setLastModify(Timestamp lastModify);

	/**
	 * @return the lastModify
	 */
	public Timestamp getLastModify();

	/**
	 * @param deleteDate
	 *            the deleteDate to set
	 */
	public void setDeleteDate(Timestamp deleteDate);

	/**
	 * @return the deleteDate
	 */
	public Timestamp getDeleteDate();
}
