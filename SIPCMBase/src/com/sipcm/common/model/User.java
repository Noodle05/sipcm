/**
 * 
 */
package com.sipcm.common.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Type;

import com.sipcm.base.model.IdBasedEntity;
import com.sipcm.base.model.TrackableEntity;
import com.sipcm.common.AccountStatus;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_user", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"email", "deletedate" }) })
@FilterDef(name = "deleteDateFilter")
@Filter(name = "deleteDateFilter", condition = "deletedate IS NULL")
@SQLDelete(sql = "UPDATE tbl_user SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
@SQLDeleteAll(sql = "UPDATE tbl_user SET deletedate = CURRENT_TIMESTAMP WHERE deletedate IS NOT NULL")
public class User implements IdBasedEntity<Long>, TrackableEntity {
	private static final long serialVersionUID = 4305835276667230335L;

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

	@Basic
	@Column(name = "first_name", length = 64, nullable = false)
	private String firstName;

	@Basic
	@Column(name = "middle_name", length = 64)
	private String middleName;

	@Basic
	@Column(name = "last_name", length = 64, nullable = false)
	private String lastName;

	@Basic
	@Column(name = "display_name", length = 64)
	private String displayName;

	@Basic
	@Column(name = "birthday")
	private Date birthDay;

	@Basic
	@Column(name = "email", length = 256, nullable = false)
	private String email;

	@Basic
	@Column(name = "username", length = 64, nullable = false, unique = true)
	private String username;

	@Type(type = "encryptedString")
	@Column(name = "password", length = 64)
	private String password;

	@Enumerated
	@Column(name = "status", nullable = false)
	private AccountStatus status;

	@Basic
	@Column(name = "sipdomain", length = 256)
	private String sipDomain;

	@Basic
	@Column(name = "sipproxy", length = 256)
	private String sipProxy;

	@Basic
	@Column(name = "sipid", length = 32, nullable = false)
	private String sipId;

	@Type(type = "encryptedString")
	@Column(name = "sippassword", length = 64, nullable = false)
	private String sipPassword;

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
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param middleName
	 *            the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		if (displayName == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(firstName);
			if (middleName != null) {
				sb.append(" ").append(middleName);
			}
			sb.append(" ").append(lastName);
			return sb.toString();
		} else {
			return displayName;
		}
	}

	/**
	 * @param birthDay
	 *            the birthDay to set
	 */
	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	/**
	 * @return the birthDay
	 */
	public Date getBirthDay() {
		return birthDay;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
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

	/**
	 * @param sipDomain
	 *            the sipDomain to set
	 */
	public void setSipDomain(String sipDomain) {
		this.sipDomain = sipDomain;
	}

	/**
	 * @return the sipDomain
	 */
	public String getSipDomain() {
		return sipDomain;
	}

	/**
	 * @param sipProxy
	 *            the sipProxy to set
	 */
	public void setSipProxy(String sipProxy) {
		this.sipProxy = sipProxy;
	}

	/**
	 * @return the sipProxy
	 */
	public String getSipProxy() {
		return sipProxy;
	}

	/**
	 * @param sipId
	 *            the sipId to set
	 */
	public void setSipId(String sipId) {
		this.sipId = sipId;
	}

	/**
	 * @return the sipId
	 */
	public String getSipId() {
		return sipId;
	}

	/**
	 * @param sipPassword
	 *            the sipPassword to set
	 */
	public void setSipPassword(String sipPassword) {
		this.sipPassword = sipPassword;
	}

	/**
	 * @return the sipPassword
	 */
	public String getSipPassword() {
		return sipPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(11, 15);
		hcb.append(email.toUpperCase());
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
		if (other == null || !(other instanceof User)) {
			return false;
		}
		final User obj = (User) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(email, obj.email);
		return eb.isEquals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("User[");
		if (id != null) {
			sb.append("id=").append(id);
		}
		sb.append("email=").append(email).append("]");
		return sb.toString();
	}
}
