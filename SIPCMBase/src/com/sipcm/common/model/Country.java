/**
 * 
 */
package com.sipcm.common.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;

import com.sipcm.base.model.IdBasedEntity;
import com.sipcm.base.model.TrackableEntity;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_country", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "name", "deletedate" }),
		@UniqueConstraint(columnNames = { "iso_3316_code", "deletedate" }) })
@FilterDef(name = "deleteDateFilter")
@Filter(name = "deleteDateFilter", condition = "deletedate IS NULL")
@SQLDelete(sql = "UPDATE tbl_country SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
@SQLDeleteAll(sql = "UPDATE tbl_country SET deletedate = CURRENT_TIMESTAMP WHERE deletedate IS NOT NULL")
public class Country implements IdBasedEntity<Integer>, TrackableEntity {
	private static final long serialVersionUID = -9217749234056452234L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Basic
	@Column(name = "createdate", nullable = false)
	private Timestamp createDate;

	@Basic
	@Column(name = "lastmodify", nullable = false)
	private Timestamp lastModify;

	@Basic
	@Column(name = "deleteDate")
	private Timestamp deleteDate;

	@Basic
	@Column(name = "name", length = 64, nullable = false)
	private String name;

	@Basic
	@Column(name = "iso_3316_code", length = 64, nullable = false)
	private String iso3316Code;

	@Basic
	@Column(name = "code", nullable = false)
	private Integer code;

	@Basic
	@Column(name = "subcode")
	private Integer subCode;

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

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

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param iso3316Code
	 *            the iso3316Code to set
	 */
	public void setIso3316Code(String iso3316Code) {
		this.iso3316Code = iso3316Code;
	}

	/**
	 * @return the iso3316Code
	 */
	public String getIso3316Code() {
		return iso3316Code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param subCode
	 *            the subCode to set
	 */
	public void setSubCode(Integer subCode) {
		this.subCode = subCode;
	}

	/**
	 * @return the subCode
	 */
	public Integer getSubCode() {
		return subCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(13, 17);
		hcb.append(name);
		hcb.append(deleteDate);
		return hcb.toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Country)) {
			return false;
		}
		final Country obj = (Country) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(name, obj.name);
		eb.append(deleteDate, obj.deleteDate);
		return eb.isEquals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Country[name=").append(name).append(",iso3316code=")
				.append(iso3316Code).append("]");
		return sb.toString();
	}
}
