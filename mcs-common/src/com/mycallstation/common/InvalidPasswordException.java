/**
 * 
 */
package com.mycallstation.common;

/**
 * @author Wei Gao
 * 
 */
public class InvalidPasswordException extends AuthenticationException {
	private static final long serialVersionUID = -251430658909898526L;

	public InvalidPasswordException() {
		super("Invalid password");
	}
}
