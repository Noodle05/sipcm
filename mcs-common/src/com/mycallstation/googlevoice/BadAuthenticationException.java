/**
 * 
 */
package com.mycallstation.googlevoice;

/**
 * @author Wei Gao
 * 
 */
public class BadAuthenticationException extends GoogleAuthenticationException {
	private static final long serialVersionUID = 3204975334112068443L;

	BadAuthenticationException() {
		super(AuthenticationErrorCode.BadAuthentication);
	}
}
