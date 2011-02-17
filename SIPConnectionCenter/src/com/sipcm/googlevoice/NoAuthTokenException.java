/**
 * 
 */
package com.sipcm.googlevoice;

import com.sipcm.common.AuthenticationException;

/**
 * @author wgao
 * 
 */
public class NoAuthTokenException extends AuthenticationException {
	private static final long serialVersionUID = -3327161238092034060L;

	public NoAuthTokenException() {
		super(
				"Cannot find authentication token. (Galx for google, for example)");
	}
}
