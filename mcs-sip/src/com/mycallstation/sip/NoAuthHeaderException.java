/**
 * 
 */
package com.mycallstation.sip;

import com.mycallstation.common.AuthenticationException;

/**
 * @author Wei Gao
 * 
 */
public class NoAuthHeaderException extends AuthenticationException {
	private static final long serialVersionUID = 4761339446034075763L;

	public NoAuthHeaderException() {
		super("Cannot find authentication header");
	}
}
