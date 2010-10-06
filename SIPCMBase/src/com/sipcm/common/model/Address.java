/**
 * 
 */
package com.sipcm.common.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "tbl_address")
@FilterDef(name = "deleteDateFilter")
@Filter(name = "deleteDateFilter", condition = "deletedate IS NULL")
@SQLDelete(sql = "UPDATE tbl_address SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
@SQLDeleteAll(sql = "UPDATE tbl_address SET deletedate = CURRENT_TIMESTAMP WHERE deletedate IS NOT NULL")
public class Address implements IdBasedEntity<Long>, TrackableEntity {
	private static final long serialVersionUID = 2939015763392613558L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Basic
	@Column(name = "createdate", nullable = false)
	private Timestamp createDate;

	@Basic
	@Column(name = "lastmodify", nullable = false)
	private Timestamp lastModify;

	@Basic
	@Column(name = "deleteDate")
	private Timestamp deleteDate;

	@Column(name = "address_line_1", length = 256, nullable = false)
	private String addressLine1;

	@Column(name = "address_ine_2", length = 256)
	private String addressLine2;

	@Column(name = "city", length = 256, nullable = false)
	private String city;

	@Column(name = "state", length = 64, nullable = false)
	private String state;

	@Column(name = "zipcode", length = 16, nullable = false)
	private String zipcode;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "country_id")
	private Country country;

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.base.model.IdBasedEntity#getId()
	 */
	@Override
	public Long getId() {
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
	 * @param addressLine1
	 *            the addressLine1 to set
	 */
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine2
	 *            the addressLine2 to set
	 */
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param zipcode
	 *            the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}
}
