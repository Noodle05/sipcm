/**
 * 
 */
package com.mycallstation.googlevoice;

/**
 * @author Wei Gao
 * 
 */
public class ServiceDisabledException extends GoogleAuthenticationException {
	private static final long serialVersionUID = -2869167366134541637L;

	ServiceDisabledException() {
		super(AuthenticationErrorCode.ServiceDisabled);
	}
}
