/**
 * 
 */
package com.mycallstation.googlevoice;

/**
 * @author wgao
 * 
 */
public class AccountDisabledException extends GoogleAuthenticationException {
	private static final long serialVersionUID = 6521265346152821786L;

	AccountDisabledException() {
		super(AuthenticationErrorCode.AccountDisabled);
	}
}
