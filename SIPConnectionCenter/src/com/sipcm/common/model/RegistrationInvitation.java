/**
 * 
 */
package com.sipcm.common.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sipcm.base.model.IdBasedEntity;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_reginvitation")
public class RegistrationInvitation implements IdBasedEntity<Integer>,
		Serializable {
	private static final long serialVersionUID = -5380141215556412792L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Basic
	@Column(name = "code", length = 36, nullable = false)
	private String code;

	@Basic
	@Column(name = "expire_date")
	private Date expireDate;

	@Basic
	@Column(name = "count")
	private int count;

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
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param expireDate
	 *            the expireDate to set
	 */
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	/**
	 * @return the expireDate
	 */
	public Date getExpireDate() {
		return expireDate;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

}
