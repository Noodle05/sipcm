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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.SQLDelete;

import com.sipcm.base.model.AbstractTrackableEntity;
import com.sipcm.base.model.IdBasedEntity;

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
	 * @see com.sipcm.base.model.IdBasedEntity#getId()
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
		HashCodeBuilder hcb = new HashCodeBuilder(15, 43);
		hcb.append(name.toUpperCase());
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
		if (other == null || !(other instanceof Role)) {
			return false;
		}
		final Role obj = (Role) other;
		EqualsBuilder eq = new EqualsBuilder();
		eq.append(name.toUpperCase(), obj.name.toUpperCase());
		eq.append(deleteDate, obj.deleteDate);
		return eq.isEquals();
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
