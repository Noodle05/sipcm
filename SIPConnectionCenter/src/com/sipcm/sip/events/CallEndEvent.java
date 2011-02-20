/**
 * 
 */
package com.sipcm.sip.events;

import java.util.Date;
import java.util.EventObject;

/**
 * @author wgao
 * 
 */
public class CallEndEvent extends EventObject {
	private static final long serialVersionUID = 369570856795151540L;

	private final Date endTime;
	private final int errorCode;
	private final String errorMessage;

	public CallEndEvent(CallStartEvent startEvent) {
		this(startEvent, 0, null);
	}

	public CallEndEvent(CallStartEvent startEvent, int errorCode,
			String errorMessage) {
		super(startEvent);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.endTime = new Date();
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return get original call start event
	 */
	public CallStartEvent getCallStartEvent() {
		return (CallStartEvent) source;
	}

	public boolean isSuccess() {
		return errorCode == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CallEndEvent[").append(source);
		if (errorCode != 0) {
			sb.append(",ErrorCode=").append(errorCode);
			if (errorMessage != null) {
				sb.append(",ErrorMessage=").append(errorMessage);
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
