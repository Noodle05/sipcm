/**
 * 
 */
package com.mycallstation.dataaccess.model;

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

import com.mycallstation.base.model.AbstractTrackableEntity;
import com.mycallstation.base.model.IdBasedEntity;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_role", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"name", "deletedate" }) })
@SQLDelete(sql = "UPDATE tbl_user SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
public class Role extends AbstractTrackableEntity implements
		IdBasedEntity<Integer>, Serializable {
	private static final long serialVersionUID = 1770689623959646857L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Basic
	@Column(name = "name", length = 64, nullable = false)
	private String name;

	@Basic
	@Column(name = "description", length = 2000)
	private String description;

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.base.model.IdBasedEntity#getId()
	 */
	@Override
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
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 15;
		int result = 43;
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
		if (other == null || !(other instanceof Role)) {
			return false;
		}
		final Role obj = (Role) other;
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
		sb.append("Role[");
		if (id != null) {
			sb.append("id=").append(id).append(",");
		}
		sb.append("name=").append(name).append("]");
		return sb.toString();
	}
}
