/**
 * 
 */
package com.mycallstation.dataaccess.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.FilterDef;

import com.mycallstation.base.model.IdBasedEntity;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_sipaddressbinding")
@FilterDef(name = "defaultFilter")
public class AddressBinding implements IdBasedEntity<Long>, Serializable {
	private static final long serialVersionUID = 6769447997175661766L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserSipProfile userSipProfile;

	@Basic
	@Column(name = "address", length = 255, nullable = false)
	private String address;

	@Basic
	@Column(name = "expires", nullable = false)
	private int expires;

	@Basic
	@Column(name = "call_id", length = 255)
	private String callId;

	@Basic
	@Column(name = "last_check")
	private Integer lastCheck;

	@Basic
	@Column(name = "remote_end", length = 255)
	private String remoteEnd;

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
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @param userSipProfile
	 *            the userSipProfile to set
	 */
	public void setUserSipProfile(UserSipProfile userSipProfile) {
		this.userSipProfile = userSipProfile;
	}

	/**
	 * @return the userSipProfile
	 */
	public UserSipProfile getUserSipProfile() {
		return userSipProfile;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param expires
	 *            the expires to set
	 */
	public void setExpires(int expires) {
		this.expires = expires;
	}

	/**
	 * @return the expires
	 */
	public int getExpires() {
		return expires;
	}

	/**
	 * @param callId
	 *            the callId to set
	 */
	public void setCallId(String callId) {
		this.callId = callId;
	}

	/**
	 * @return the callId
	 */
	public String getCallId() {
		return callId;
	}

	/**
	 * @param lastCheck
	 *            the lastCheck to set
	 */
	public void setLastCheck(Integer lastCheck) {
		this.lastCheck = lastCheck;
	}

	/**
	 * @return the lastCheck
	 */
	public Integer getLastCheck() {
		return lastCheck;
	}

	/**
	 * @param remoteEnd
	 *            the remoteEnd to set
	 */
	public void setRemoteEnd(String remoteEnd) {
		this.remoteEnd = remoteEnd;
	}

	/**
	 * @return the remoteEnd
	 */
	public String getRemoteEnd() {
		return remoteEnd;
	}

	@Override
	public int hashCode() {
		final int prime = 33;
		int result = 17;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result
				+ ((getCallId() == null) ? 0 : getCallId().hashCode());
		result = prime * result
				+ ((remoteEnd == null) ? 0 : remoteEnd.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof AddressBinding)) {
			return false;
		}
		final AddressBinding obj = (AddressBinding) other;
		if (address == null) {
			if (obj.address != null) {
				return false;
			}
		} else if (!address.equals(obj.address)) {
			return false;
		}
		if (callId == null) {
			if (obj.callId != null) {
				return false;
			}
		} else if (!callId.equals(obj.callId)) {
			return false;
		}
		if (remoteEnd == null) {
			if (obj.remoteEnd != null) {
				return false;
			}
		} else if (!remoteEnd.equals(obj.remoteEnd)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AddressBinding[").append("Address=").append(address)
				.append(",Expires=").append(expires).append(",RemoteEnd=")
				.append(remoteEnd).append(",CallId=").append(callId)
				.append("]");
		return sb.toString();
	}
}
