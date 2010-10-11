/**
 * 
 */
package com.sipcm.sip.model;

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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;

import com.sipcm.base.model.AbstractTrackableEntity;
import com.sipcm.base.model.IdBasedEntity;
import com.sipcm.base.model.TrackableEntity;
import com.sipcm.common.model.User;
import com.sipcm.sip.VoipAccountType;

/**
 * @author wgao
 * 
 */
@Entity
@Table(name = "tbl_uservoipaccount", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"user_id", "voipvendor_id", "deletedate" }) })
@SQLDelete(sql = "UPDATE tbl_uservoipaccount SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
public class UserVoipAccount extends AbstractTrackableEntity implements
		TrackableEntity, IdBasedEntity<Long> {
	private static final long serialVersionUID = 5445967647166910516L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "user_id")
	private User ownser;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "voipvendor_id")
	private VoipVendor voipVendor;

	@Basic
	@Column(name = "account", length = 256, nullable = false)
	private String account;

	@Type(type = "encryptedString")
	@Column(name = "password", length = 256, nullable = false)
	private String password;

	@Enumerated
	@Column(name = "type", nullable = false)
	private VoipAccountType type;

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
	 * @param ownser
	 *            the ownser to set
	 */
	public void setOwnser(User ownser) {
		this.ownser = ownser;
	}

	/**
	 * @return the ownser
	 */
	public User getOwnser() {
		return ownser;
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
}
