/**
 * 
 */
package com.sipcm.sip.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(name = "tbl_sipprovider", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "name", "deletedate" }),
		@UniqueConstraint(columnNames = { "domainname", "deletedate" }) })
@FilterDef(name = "deleteDateFilter")
@Filter(name = "deleteDateFilter", condition = "deletedate IS NULL")
@SQLDelete(sql = "UPDATE tbl_sipprovider SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
@SQLDeleteAll(sql = "UPDATE tbl_sipprovider SET deletedate = CURRENT_TIMESTAMP WHERE deletedate IS NOT NULL")
public class SIPProvider implements TrackableEntity, IdBasedEntity<Integer> {
	private static final long serialVersionUID = -6112174515534743458L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Integer id;

	@Basic
	@Column(name = "createdate", nullable = false)
	private Timestamp createDate;

	@Basic
	@Column(name = "lastmodify", nullable = false)
	private Timestamp lastModify;

	@Basic
	@Column(name = "deletedate")
	private Timestamp deleteDate;

	@Basic
	@Column(name = "name", length = 64, nullable = false)
	private String name;

	@Basic
	@Column(name = "domainname", length = 256, nullable = false)
	private String domain;

	@Basic
	@Column(name = "name", length = 256)
	private String proxy;

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
	 * @param createDate
	 *            the createDate to set
	 */
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the createDate
	 */
	public Timestamp getCreateDate() {
		return createDate;
	}

	/**
	 * @param lastModify
	 *            the lastModify to set
	 */
	public void setLastModify(Timestamp lastModify) {
		this.lastModify = lastModify;
	}

	/**
	 * @return the lastModify
	 */
	public Timestamp getLastModify() {
		return lastModify;
	}

	/**
	 * @param deleteDate
	 *            the deleteDate to set
	 */
	public void setDeleteDate(Timestamp deleteDate) {
		this.deleteDate = deleteDate;
	}

	/**
	 * @return the deleteDate
	 */
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
}
