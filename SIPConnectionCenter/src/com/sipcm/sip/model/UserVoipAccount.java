/**
 * 
 */
package com.sipcm.sip.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;

import com.sipcm.base.model.AbstractTrackableEntity;
import com.sipcm.base.model.IdBasedEntity;
import com.sipcm.sip.VoipAccountType;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_uservoipaccount", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"user_id", "name" }) })
@SQLDelete(sql = "UPDATE tbl_uservoipaccount SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
public class UserVoipAccount extends AbstractTrackableEntity implements
		IdBasedEntity<Long>, Serializable {
	private static final long serialVersionUID = 5445967647166910516L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private UserSipProfile owner;

	@Basic
	@Column(name = "name", length = 32, nullable = false)
	private String name;

	@ManyToOne(optional = false)
	@JoinColumn(name = "voipvendor_id")
	private VoipVendor voipVendor;

	@Basic
	@Column(name = "account", length = 256, nullable = false)
	private String account;

	@Type(type = "encryptedString")
	@Column(name = "password", length = 256, nullable = false)
	private String password;

	@Basic
	@Column(name = "phone_number", length = 32)
	private String phoneNumber;

	@Basic
	@Column(name = "callback_number", length = 32)
	private String callBackNumber;

	@Enumerated
	@Column(name = "type", nullable = false)
	private VoipAccountType type;

	@Basic
	@Column(name = "online", nullable = false)
	private boolean online;

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
	public Long getId() {
		return id;
	}

	/**
	 * @param owner
	 *            the ownser to set
	 */
	public void setOwnser(UserSipProfile owner) {
		this.owner = owner;
	}

	/**
	 * @return the ownser
	 */
	public UserSipProfile getOwner() {
		return owner;
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
	 * @param voipVendor
	 *            the voipVendor to set
	 */
	public void setVoipVendor(VoipVendor voipVendor) {
		this.voipVendor = voipVendor;
	}

	/**
	 * @return the voipVendor
	 */
	public VoipVendor getVoipVendor() {
		return voipVendor;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
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
	 * @param callBackNumber
	 *            the callBackNumber to set
	 */
	public void setCallBackNumber(String callBackNumber) {
		this.callBackNumber = callBackNumber;
	}

	/**
	 * @return the callBackNumber
	 */
	public String getCallBackNumber() {
		return callBackNumber;
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
	 * @param type
	 *            the type to set
	 */
	public void setType(VoipAccountType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public VoipAccountType getType() {
		return type;
	}

	/**
	 * @param online
	 *            the online to set
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return online;
	}

	@Override
	public int hashCode() {
		final int prime = 13;
		int result = 57;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result
				+ ((name == null) ? 0 : name.toUpperCase().hashCode());
		result = prime * result
				+ ((deleteDate == null) ? 0 : deleteDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof UserVoipAccount)) {
			return false;
		}
		final UserVoipAccount obj = (UserVoipAccount) other;
		if (owner == null) {
			if (obj.owner != null) {
				return false;
			}
		} else if (!owner.equals(obj.owner)) {
			return false;
		}
		if (name == null) {
			if (obj.name != null) {
				return false;
			}
		} else if (!name.equalsIgnoreCase(obj.name)) {
			return false;
		}
		if (deleteDate == null) {
			if (obj.deleteDate != null) {
				return false;
			}
		} else if (!deleteDate.equals(obj.deleteDate)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserVoipAccount[");
		if (id != null) {
			sb.append("id=").append(id).append(",");
		}
		sb.append("owner=").append(owner).append(",name=").append(name)
				.append(",account=").append(account).append(",vendor=")
				.append(voipVendor).append("]");
		return sb.toString();
	}
}
