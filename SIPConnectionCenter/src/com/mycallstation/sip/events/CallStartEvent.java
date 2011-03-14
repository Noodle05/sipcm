/**
 * 
 */
package com.mycallstation.sip.events;

import java.util.Date;
import java.util.EventObject;

import com.mycallstation.sip.model.UserSipProfile;
import com.mycallstation.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public class CallStartEvent extends EventObject {
	private static final long serialVersionUID = 369570856795151540L;

	private volatile Date startTime;
	private final UserSipProfile userSipProfile;
	private final UserVoipAccount account;

	public CallStartEvent(UserSipProfile userSipProfile, String partner) {
		this(userSipProfile, null, partner);
	}

	public CallStartEvent(UserVoipAccount account, String partner) {
		this(null, account, partner);
	}

	public CallStartEvent(UserSipProfile userSipProfile,
			UserVoipAccount account, String partner) {
		super(partner);
		this.userSipProfile = userSipProfile == null ? (account == null ? null
				: account.getOwner()) : userSipProfile;
		this.account = account;
		this.startTime = new Date();
	}

	/**
	 * @param startTime
	 *            the startTime
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
	 * @return the partner
	 */
	public String getPartner() {
		return (String) source;
	}

	/**
	 * @return the userSipProfile
	 */
	public UserSipProfile getUserSipProfile() {
		return userSipProfile;
	}

	/**
	 * @return the account
	 */
	public UserVoipAccount getAccount() {
		return account;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CallStartEvent[User=").append(userSipProfile)
				.append(",Partner=").append(source);
		if (account != null) {
			sb.append(",Account=").append(account);
		}
		sb.append("]");
		return sb.toString();
	}
}
