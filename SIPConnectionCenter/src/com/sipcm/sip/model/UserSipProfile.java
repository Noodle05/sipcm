/**
 * 
 */
package com.sipcm.sip.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.QueryHint;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.sipcm.base.model.IdBasedEntity;
import com.sipcm.common.OnlineStatus;
import com.sipcm.common.PhoneNumberStatus;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@GenericGenerator(name = "userSipProfileGenerator", strategy = "foreign", parameters = @Parameter(name = "property", value = "owner"))
@SqlResultSetMapping(name = "profileId", columns = @ColumnResult(name = "id"))
@NamedNativeQuery(name = "checkAddressBindingExpires", query = "call AddressBindingExpires()", resultSetMapping = "profileId", hints = { @QueryHint(name = "org.hibernate.callable", value = "true") })
@Entity
@Table(name = "tbl_usersipprofile")
public class UserSipProfile implements IdBasedEntity<Long>, Serializable {
	private static final long serialVersionUID = 6413404008060974272L;

	@Id
	@GeneratedValue(generator = "userSipProfileGenerator")
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@OneToOne
	@PrimaryKeyJoinColumn
	private User owner;

	@Enumerated
	@Column(name = "sipstatus", nullable = false)
	private OnlineStatus sipStatus;

	@Basic
	@Column(name = "phonenumber", length = 32, nullable = false)
	private String phoneNumber;

	@Basic
	@Column(name = "area_code", length = 10)
	private String defaultAreaCode;

	@Enumerated
	@Column(name = "phonenumberstatus")
	private PhoneNumberStatus phoneNumberStatus;

	@Basic
	@Column(name = "allow_local_directly", nullable = false)
	private boolean allowLocalDirectly;

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

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param defaultAreaCode
	 *            the defaultAreaCode to set
	 */
	public void setDefaultAreaCode(String defaultAreaCode) {
		this.defaultAreaCode = defaultAreaCode;
	}

	/**
	 * @return the defaultAreaCode
	 */
	public String getDefaultAreaCode() {
		return defaultAreaCode;
	}

	/**
	 * @param phoneNumberStatus
	 *            the phoneNumberStatus to set
	 */
	public void setPhoneNumberStatus(PhoneNumberStatus phoneNumberStatus) {
		this.phoneNumberStatus = phoneNumberStatus;
	}

	/**
	 * @return the phoneNumberStatus
	 */
	public PhoneNumberStatus getPhoneNumberStatus() {
		return phoneNumberStatus;
	}

	/**
	 * @param allowLocalDirectly
	 *            the allowLocalDirectly to set
	 */
	public void setAllowLocalDirectly(boolean allowLocalDirectly) {
		this.allowLocalDirectly = allowLocalDirectly;
	}

	/**
	 * @return the allowLocalDirectly
	 */
	public boolean isAllowLocalDirectly() {
		return allowLocalDirectly;
	}

	public String getDisplayName() {
		if (owner != null) {
			return owner.getUserDisplayName();
		} else {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 43;
		int result = 9;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof UserSipProfile)) {
			return false;
		}
		final UserSipProfile obj = (UserSipProfile) other;
		if (owner == null) {
			if (owner != null) {
				return false;
			}
		} else if (!owner.equals(obj.owner)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UserSipProfile[" + (owner == null ? "" : owner.toString())
				+ "]";
	}
}
