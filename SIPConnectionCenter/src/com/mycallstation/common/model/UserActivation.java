/**
 * 
 */
package com.mycallstation.common.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.mycallstation.base.model.IdBasedEntity;
import com.mycallstation.common.ActiveMethod;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_useractivation")
public class UserActivation implements IdBasedEntity<Long>, Serializable {
	private static final long serialVersionUID = 6592955111151209132L;

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "owner"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@OneToOne
	@PrimaryKeyJoinColumn
	private User owner;

	@Basic
	@Column(name = "active_code", length = 32, nullable = false)
	private String activeCode;

	@Basic
	@Column(name = "expire_date", nullable = false)
	private Date expireDate;

	@Enumerated
	@Column(name = "method", nullable = false)
	private ActiveMethod method;

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * @return the owner
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * @param activeCode
	 *            the activeCode to set
	 */
	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	/**
	 * @return the activeCode
	 */
	public String getActiveCode() {
		return activeCode;
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
	 * @param method
	 *            the method to set
	 */
	public void setMethod(ActiveMethod method) {
		this.method = method;
	}

	/**
	 * @return the method
	 */
	public ActiveMethod getMethod() {
		return method;
	}
}
