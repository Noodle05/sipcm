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
	public Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	public UserSipProfile user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "voipaccount_id")
	public UserVoipAccount voipAccount;

	@Enumerated
	@Column(name = "type", nullable = false)
	public CallType type;

	@Basic
	@Column(name = "target", length = 255, nullable = false)
	public String target;

	@Enumerated
	@Column(name = "status", nullable = false)
	public CallStatus status;

	@Basic
	@Column(name = "errorcode")
	public Integer errorCode;

	@Basic
	@Column(name = "errorMessage", length = 255)
	public String errorMessage;

	@Basic
	@Column(name = "starttime", nullable = false)
	public Date startTime;

	@Basic
	@Column(name = "endtime")
	public Date endTime;
}
