/**
 * 
 */
package com.mycallstation.dataaccess.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.SQLDelete;

import com.mycallstation.base.model.AbstractTrackableEntity;
import com.mycallstation.base.model.IdBasedEntity;
import com.mycallstation.constant.VoipVendorType;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_voipvendor", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "name", "deletedate" }),
		@UniqueConstraint(columnNames = { "domainname", "deletedate" }) })
@SQLDelete(sql = "UPDATE tbl_voipvendor SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
public class VoipVendor extends AbstractTrackableEntity implements
		IdBasedEntity<Integer>, Serializable {
	private static final long serialVersionUID = -6112174515534743458L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Basic
	@Column(name = "name", length = 64, nullable = false)
	private String name;

	@Basic
	@Column(name = "domainname", length = 255, nullable = false)
	private String domain;

	@Basic
	@Column(name = "proxy", length = 256)
	private String proxy;

	@Enumerated
	@Column(name = "type", nullable = false)
	private VoipVendorType type;

	@Basic
	@Column(name = "expires")
	private Integer defaultExpires;

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
	 * @param domain
	 *            the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param proxy
	 *            the proxy to set
	 */
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	/**
	 * @return the proxy
	 */
	public String getProxy() {
		return proxy;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(VoipVendorType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public VoipVendorType getType() {
		return type;
	}

	/**
	 * @param defaultExpires
	 *            the defaultExpires to set
	 */
	public void setDefaultExpires(Integer defaultExpires) {
		this.defaultExpires = defaultExpires;
	}

	/**
	 * @return the defaultExpires
	 */
	public Integer getDefaultExpires() {
		return defaultExpires;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 7;
		int result = 35;
		result = prime * result
				+ ((name == null) ? 0 : name.toUpperCase().hashCode());
		if (deleteDate != null) {
			result = prime * result + deleteDate.hashCode();
		}
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
		if (other == null || !(other instanceof VoipVendor)) {
			return false;
		}
		final VoipVendor obj = (VoipVendor) other;
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
		sb.append("VoipVendor[");
		if (id != null) {
			sb.append("id=").append(id).append(",");
		}
		sb.append("name=").append(name).append("]");
		return sb.toString();
	}
}
