/**
 * 
 */
package com.mycallstation.common;

/**
 * @author Wei Gao
 * 
 */
public class NoSuchUserException extends AuthenticationException {
	private static final long serialVersionUID = -2464028343412820268L;

	private String username;

	public NoSuchUserException(String username) {
		super("No such user. User name: " + username);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
