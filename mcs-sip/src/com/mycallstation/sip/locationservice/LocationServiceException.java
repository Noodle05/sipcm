/**
 * 
 */
package com.mycallstation.sip.locationservice;

/**
 * @author Wei Gao
 * 
 */
public class LocationServiceException extends Exception {
	private static final long serialVersionUID = 2186408266069899506L;

	public LocationServiceException() {
		super();
	}

	public LocationServiceException(Throwable cause) {
		super(cause);
	}

	public LocationServiceException(String message) {
		super(message);
	}

	public LocationServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
