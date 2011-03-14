/**
 * 
 */
package com.mycallstation.sip.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.QueryHint;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.mobicents.servlet.sip.message.SipServletResponseImpl;

import com.mycallstation.base.model.AbstractTrackableEntity;
import com.mycallstation.base.model.IdBasedEntity;
import com.mycallstation.sip.VoipAccountType;

/**
 * @author wgao
 * 
 */
@SqlResultSetMapping(name = "accountId", columns = @ColumnResult(name = "id"))
@NamedNativeQuery(name = "registerClientExpires", query = "call RegisterClientExpires(:minExpires)", resultSetMapping = "accountId", hints = { @QueryHint(name = "org.hibernate.callable", value = "true") })
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

	@Basic
	@Column(name = "callback_type")
	private Integer callBackType;

	@Enumerated
	@Column(name = "type", nullable = false)
	private VoipAccountType type;

	@Basic
	@Column(name = "register_expires", insertable = false, updatable = false)
	private Integer regExpires;

	@Basic
	@Column(name = "last_check", insertable = false, updatable = false)
	private Integer lastCheck;

	@Basic(fetch = FetchType.LAZY)
	@Column(name = "auth_response", insertable = false, updatable = false)
	private SipServletResponseImpl authResponse;

	@Basic
	@Column(name = "error_code", insertable = false, updatable = false)
	private int errorCode;

	@Basic
	@Column(name = "error_message", insertable = false, updatable = false)
	private String errorMessage;

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
	 * @see com.mycallstation.base.model.IdBasedEntity#getId()
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(UserSipProfile owner) {
		this.owner = owner;
	}

	/**
	 * @return the owner
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
	 * @param callBackType
	 *            the callBackType to set
	 */
	public void setCallBackType(Integer callBackType) {
		this.callBackType = callBackType;
	}

	/**
	 * @return the callBackType
	 */
	public Integer getCallBackType() {
		return callBackType;
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
	 * @param regExpires
	 *            the regExpires to set
	 */
	public void setRegExpires(Integer regExpires) {
		this.regExpires = regExpires;
	}

	/**
	 * @return the regExpires
	 */
	public Integer getRegExpires() {
		return regExpires;
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
	 * @param authResponse
	 *            the authResponse to set
	 */
	public void setAuthResponse(SipServletResponseImpl authResponse) {
		this.authResponse = authResponse;
	}

	/**
	 * @return the authResponse
	 */
	public SipServletResponseImpl getAuthResponse() {
		return authResponse;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorInfo() {
		if (errorCode != 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(errorCode);
			if (errorMessage != null) {
				sb.append(":").append(errorMessage);
			}
			return sb.toString();
		}
		return null;
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
