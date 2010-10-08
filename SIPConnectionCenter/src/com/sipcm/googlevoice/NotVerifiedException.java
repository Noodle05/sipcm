/**
 * 
 */
package com.sipcm.googlevoice;

/**
 * @author wgao
 * 
 */
public class NotVerifiedException extends GoogleAuthenticationException {
	private static final long serialVersionUID = 3160461379844378020L;

	NotVerifiedException() {
		super(AuthenticationErrorCode.NotVerified);
	}
}
