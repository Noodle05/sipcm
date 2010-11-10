/**
 * 
 */
package com.sipcm.sip.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.sipcm.base.model.AbstractTrackableEntity;
import com.sipcm.base.model.IdBasedEntity;
import com.sipcm.common.OnlineStatus;
import com.sipcm.common.PhoneNumberStatus;
import com.sipcm.common.model.User;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_usersipprofile", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"phonenumber", "deletedate" }) })
@SQLDelete(sql = "UPDATE tbl_usersipprofile SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
public class UserSipProfile extends AbstractTrackableEntity implements
		IdBasedEntity<Long>, Serializable {
	private static final long serialVersionUID = 6413404008060974272L;

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "owner"))
	@Id
	@GeneratedValue(generator = "generator")
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

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@Where(clause = "deletedate is null")
	private Set<UserVoipAccount> voipAccounts;

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

	/**
	 * @param voipAccounts
	 *            the voipAccounts to set
	 */
	public void setSipAccounts(Set<UserVoipAccount> voipAccounts) {
		this.voipAccounts = voipAccounts;
	}

	/**
	 * @return the voipAccounts
	 */
	public Set<UserVoipAccount> getVoipAccounts() {
		return voipAccounts;
	}

	public String getDisplayName() {
		if (owner != null) {
			return owner.getDisplayName();
		} else {
			return null;
		}
	}
}
