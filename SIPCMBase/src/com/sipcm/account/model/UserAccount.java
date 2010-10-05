/**
 * 
 */
package com.sipcm.account.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.jasypt.hibernate.type.EncryptedStringType;

import com.sipcm.account.AccountStatus;
import com.sipcm.base.model.IdBasedEntity;
import com.sipcm.entity.User;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_account")
@TypeDef(name = "encryptedString", typeClass = EncryptedStringType.class, parameters = { @Parameter(name = "encryptorRegisteredName", value = "sipHibernateStringEncryptor") })
public class UserAccount implements IdBasedEntity<Long>, Serializable {
	private static final long serialVersionUID = 4305835276667230335L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "USERNMAE", length = 64, nullable = false, unique = true)
	private String username;

	@Type(type = "encryptedString")
	@Column(name = "PASSWORD", length = 64)
	private String password;

	@Enumerated
	@Column(name = "STATUS", nullable = false)
	private AccountStatus status;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "USER_ID", unique = true)
	public User owner;

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

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public AccountStatus getStatus() {
		return status;
	}
}
