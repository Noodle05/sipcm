/**
 * 
 */
package com.sipcm.entity;

import java.io.Serializable;

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
@Table(name = "tbl_country")
public class Country implements IdBasedEntity<Integer>, Serializable {
	private static final long serialVersionUID = -9217749234056452234L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Integer id;

	@Basic
	@Column(name = "NAME", length = 64, nullable = false)
	private String name;

	@Basic
	@Column(name = "ISO_3316_CODE", length = 64, nullable = false)
	private String iso3316Code;

	@Basic
	@Column(name = "CODE", nullable = false)
	private Integer code;

	@Basic
	@Column(name = "SUBCODE")
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
}
