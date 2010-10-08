/**
 * 
 */
package com.sipcm.googlevoice;

/**
 * @author wgao
 * 
 */
public class ServiceDisabledException extends GoogleAuthenticationException {
	private static final long serialVersionUID = -2869167366134541637L;

	ServiceDisabledException() {
		super(AuthenticationErrorCode.ServiceDisabled);
	}
}
