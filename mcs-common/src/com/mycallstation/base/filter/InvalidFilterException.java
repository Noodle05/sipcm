/**
 * 
 */
package com.mycallstation.base.filter;

/**
 * @author Wei Gao
 * 
 */
public class InvalidFilterException extends RuntimeException {
	private static final long serialVersionUID = 8917550827075587104L;

	public InvalidFilterException() {
		super();
	}

	public InvalidFilterException(String msg) {
		super(msg);
	}

	public InvalidFilterException(Exception cause) {
		super(cause);
	}

	public InvalidFilterException(String msg, Exception cause) {
		super(msg, cause);
	}
}
