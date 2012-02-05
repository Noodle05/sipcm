/**
 * 
 */
package com.mycallstation.googlevoice;

import com.mycallstation.common.AuthenticationException;

/**
 * @author Wei Gao
 * 
 */
public class NoRnrSeException extends AuthenticationException {
	private static final long serialVersionUID = 2950762963984361338L;

	public NoRnrSeException() {
		super("Cannot find RNR for google authentication.");
	}
}
