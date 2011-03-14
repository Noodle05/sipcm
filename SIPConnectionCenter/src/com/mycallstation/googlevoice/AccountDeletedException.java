/**
 * 
 */
package com.mycallstation.googlevoice;

/**
 * @author wgao
 * 
 */
public class AccountDeletedException extends GoogleAuthenticationException {
	private static final long serialVersionUID = -4664855794456891444L;

	AccountDeletedException() {
		super(AuthenticationErrorCode.AccountDeleted);
	}

	@Override
	public String getMessage() {
		return null;
	}
}
