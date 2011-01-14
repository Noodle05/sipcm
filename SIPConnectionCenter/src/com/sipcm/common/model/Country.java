/**
 * 
 */
package com.sipcm.common.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.SQLDelete;

import com.sipcm.base.model.AbstractTrackableEntity;
import com.sipcm.base.model.IdBasedEntity;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_country", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "name", "deletedate" }),
		@UniqueConstraint(columnNames = { "iso_3316_code", "deletedate" }) })
@SQLDelete(sql = "UPDATE tbl_country SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
public class Country extends AbstractTrackableEntity implements
		IdBasedEntity<Integer>, Serializable {
	private static final long serialVersionUID = -9217749234056452234L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

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
		final int prime = 13;
		int result = 15;
		result = prime * result
				+ ((name == null) ? 0 : name.toUpperCase().hashCode());
		result = prime * result
				+ ((deleteDate == null) ? 0 : deleteDate.hashCode());
		return result;
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
		if (name == null) {
			if (obj.name != null) {
				return false;
			}
		} else if (!name.equalsIgnoreCase(obj.name)) {
			return false;
		}
		if (deleteDate == null) {
			if (obj.deleteDate != null) {
				return false;
			}
		} else if (!deleteDate.equals(obj.deleteDate)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Country[");
		if (id != null) {
			sb.append("id=").append(id).append(",");
		}
		sb.append("name=").append(name).append(",iso3316code=")
				.append(iso3316Code).append("]");
		return sb.toString();
	}
}
