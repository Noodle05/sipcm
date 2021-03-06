/**
 * 
 */
package com.mycallstation.googlevoice;

/**
 * @author Wei Gao
 * 
 */
public class ServiceUnavailableException extends GoogleAuthenticationException {
	private static final long serialVersionUID = -2270054081539189652L;

	ServiceUnavailableException() {
		super(AuthenticationErrorCode.ServiceUnavailable);
	}
}
