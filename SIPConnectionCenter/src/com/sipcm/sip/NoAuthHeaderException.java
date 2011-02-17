/**
 * 
 */
package com.sipcm.sip;

import com.sipcm.common.AuthenticationException;

/**
 * @author wgao
 * 
 */
public class NoAuthHeaderException extends AuthenticationException {
	private static final long serialVersionUID = 4761339446034075763L;

	public NoAuthHeaderException() {
		super("Cannot find authentication header");
	}
}
