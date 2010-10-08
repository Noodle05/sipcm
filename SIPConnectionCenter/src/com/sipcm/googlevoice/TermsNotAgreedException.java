/**
 * 
 */
package com.sipcm.googlevoice;

/**
 * @author wgao
 * 
 */
public class TermsNotAgreedException extends GoogleAuthenticationException {
	private static final long serialVersionUID = 7284756117151637883L;

	TermsNotAgreedException() {
		super(AuthenticationErrorCode.TermsNotAgreed);
	}
}
