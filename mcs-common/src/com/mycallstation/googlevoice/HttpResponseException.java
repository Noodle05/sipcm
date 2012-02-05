/**
 * 
 */
package com.mycallstation.googlevoice;

/**
 * @author Wei Gao
 * 
 */
public class HttpResponseException extends Exception {
	private static final long serialVersionUID = -2208756285947563342L;

	private int httpStatus;

	public HttpResponseException(int httpStatus) {
		super();
		this.httpStatus = httpStatus;
	}

	public HttpResponseException(int httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Http Status: ").append(httpStatus).append(", Message: ")
				.append(super.getMessage());
		return sb.toString();
	}
}
