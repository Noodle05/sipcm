/**
 * 
 */
package com.sipcm.sip.vendor;

import java.io.Serializable;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.sip.AuthInfo;

import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
public class ClientRegisterHolder implements Serializable, Runnable {
	private static final long serialVersionUID = -1838916731280534574L;

	private ScheduledExecutorService threadPool;

	private final UserSipProfile userSipProfile;
	private final UserVoipAccount account;
	private AuthInfo authInfo;
	private int expiresTime;

	public void run() {

	}

	public ClientRegisterHolder(UserSipProfile userSipProfile,
			UserVoipAccount account) {
		this.userSipProfile = userSipProfile;
		this.account = account;
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

	/**
	 * @param authInfo
	 *            the authInfo to set
	 */
	public void setAuthInfo(AuthInfo authInfo) {
		this.authInfo = authInfo;
	}

	/**
	 * @return the authInfo
	 */
	public AuthInfo getAuthInfo() {
		return authInfo;
	}

	/**
	 * @param expiresTime
	 *            the expiresTime to set
	 */
	public void setExpiresTime(int expiresTime) {
		this.expiresTime = expiresTime;
	}

	/**
	 * @return the expiresTime
	 */
	public int getExpiresTime() {
		return expiresTime;
	}

}
