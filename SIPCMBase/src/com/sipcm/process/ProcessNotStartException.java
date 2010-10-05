/**
 * 
 */
package com.sipcm.process;

/**
 * @author Jack
 * 
 */
public class ProcessNotStartException extends IllegalStateException {
	private static final long serialVersionUID = -1627849097572861220L;

	public ProcessNotStartException() {
		super();
	}

	public ProcessNotStartException(String s) {
		super(s);
	}

	public ProcessNotStartException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessNotStartException(Throwable cause) {
		super(cause);
	}
}
