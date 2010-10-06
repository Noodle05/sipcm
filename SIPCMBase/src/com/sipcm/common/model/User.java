/**
 * 
 */
package com.sipcm.common.model;

import java.io.Serializable;
import java.sql.Date;

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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;

import com.sipcm.base.model.AbstractTrackableEntity;
import com.sipcm.base.model.IdBasedEntity;
import com.sipcm.common.AccountStatus;
import com.sipcm.common.OnlineStatus;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_user", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "email", "deletedate" }),
		@UniqueConstraint(columnNames = { "username", "deletedate" }),
		@UniqueConstraint(columnNames = { "sipid", "deletedate" }) })
@SQLDelete(sql = "UPDATE tbl_user SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
public class User extends AbstractTrackableEntity implements
		IdBasedEntity<Long>, Serializable {
	private static final long serialVersionUID = 4305835276667230335L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

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
	@Column(name = "username", length = 64, nullable = false)
	private String username;

	@Type(type = "encryptedString")
	@Column(name = "password", length = 64)
	private String password;

	@Enumerated
	@Column(name = "status", nullable = false)
	private AccountStatus status;

	@Basic
	@Column(name = "sipid", length = 32, nullable = false)
	private String sipId;

	@Type(type = "encryptedString")
	@Column(name = "sippassword", length = 64, nullable = false)
	private String sipPassword;

	@Enumerated
	@Column(name = "sipstatus", nullable = false)
	private OnlineStatus sipStatus;

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

	/**
	 * @param sipStatus
	 *            the sipStatus to set
	 */
	public void setSipStatus(OnlineStatus sipStatus) {
		this.sipStatus = sipStatus;
	}

	/**
	 * @return the sipStatus
	 */
	public OnlineStatus getSipStatus() {
		return sipStatus;
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
		eb.append(email.toUpperCase(), obj.email.toUpperCase());
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
			sb.append("id=").append(id).append(",");
		}
		sb.append("displayname=").append(getDisplayName()).append(",email=")
				.append(email).append("]");
		return sb.toString();
	}
}
