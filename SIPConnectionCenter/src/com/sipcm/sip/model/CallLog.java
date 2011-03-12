/**
 * 
 */
package com.sipcm.sip.model;

import java.io.Serializable;
import java.util.Date;

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

import com.sipcm.sip.CallStatus;
import com.sipcm.sip.CallType;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_calllog")
public class CallLog implements Serializable {
	private static final long serialVersionUID = -7776158599758180354L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private UserSipProfile owner;

	@ManyToOne()
	@JoinColumn(name = "voipaccount_id")
	private UserVoipAccount voipAccount;

	@Enumerated
	@Column(name = "type", nullable = false)
	private CallType type;

	@Basic
	@Column(name = "partner", length = 255, nullable = false)
	private String partner;

	@Enumerated
	@Column(name = "status", nullable = false)
	private CallStatus status;

	@Basic
	@Column(name = "error_code")
	private int errorCode;

	@Basic
	@Column(name = "error_message", length = 2000)
	private String errorMessage;

	@Basic
	@Column(name = "starttime", nullable = false)
	private Date startTime;

	@Basic
	@Column(name = "duration")
	private Long duration;

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
	 * @param voipAccount
	 *            the voipAccount to set
	 */
	public void setVoipAccount(UserVoipAccount voipAccount) {
		this.voipAccount = voipAccount;
	}

	/**
	 * @return the voipAccount
	 */
	public UserVoipAccount getVoipAccount() {
		return voipAccount;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(CallType type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public CallType getType() {
		return type;
	}

	/**
	 * @param partner
	 *            the partner to set
	 */
	public void setPartner(String partner) {
		this.partner = partner;
	}

	/**
	 * @return the partner
	 */
	public String getPartner() {
		return partner;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(CallStatus status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public CallStatus getStatus() {
		return status;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public Integer getErrorCode() {
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

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	/**
	 * @return the duration
	 */
	public Long getDuration() {
		return duration;
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
}
