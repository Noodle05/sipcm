/**
 * 
 */
package com.sipcm.common;

/**
 * @author wgao
 * 
 */
public class AuthenticationException extends Exception {
	private static final long serialVersionUID = -1374832653580818782L;

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
