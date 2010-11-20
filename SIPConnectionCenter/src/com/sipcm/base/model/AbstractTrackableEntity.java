/**
 * 
 */
package com.sipcm.base.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

/**
 * @author wgao
 * 
 */
@MappedSuperclass
@FilterDef(name = "defaultFilter")
@Filter(name = "defaultFilter", condition = "deletedate = 0")
public abstract class AbstractTrackableEntity implements TrackableEntity {
	private static final long serialVersionUID = 1902546028219639721L;

	@Basic
	@Column(name = "createdate", insertable = true, updatable = false)
	protected Timestamp createDate;

	@Version
	@Column(name = "lastmodify", insertable = false, updatable = false)
	protected Timestamp lastModify;

	@Basic
	@Column(name = "deletedate", insertable = false, updatable = false)
	protected Timestamp deleteDate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.model.TrackableEntity#setCreateDate(java.sql.Timestamp)
	 */
	@Override
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.model.TrackableEntity#getCreateDate()
	 */
	@Override
	public Timestamp getCreateDate() {
		return createDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.model.TrackableEntity#setLastModify(java.sql.Timestamp)
	 */
	@Override
	public void setLastModify(Timestamp lastModify) {
		this.lastModify = lastModify;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.model.TrackableEntity#getLastModify()
	 */
	@Override
	public Timestamp getLastModify() {
		return lastModify;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.base.model.TrackableEntity#setDeleteDate(java.sql.Timestamp)
	 */
	@Override
	public void setDeleteDate(Timestamp deleteDate) {
		this.deleteDate = deleteDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.model.TrackableEntity#getDeleteDate()
	 */
	@Override
	public Timestamp getDeleteDate() {
		return deleteDate;
	}
}
