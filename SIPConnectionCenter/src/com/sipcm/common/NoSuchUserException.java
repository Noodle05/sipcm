/**
 * 
 */
package com.sipcm.common;


/**
 * @author wgao
 * 
 */
public class NoSuchUserException extends AuthenticationException {
	private static final long serialVersionUID = -2464028343412820268L;

	private String username;

	public NoSuchUserException(String username) {
		super();
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}