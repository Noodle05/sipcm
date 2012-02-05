/**
 * 
 */
package com.mycallstation.googlevoice.setting;

import java.io.Serializable;

/**
 * @author Wei Gao
 * 
 */
public class Time implements Serializable {
	private static final long serialVersionUID = 5295679858736696007L;
	private String startTime;
	private String endTime;

	public Time() {
		startTime = "9:00am";
		endTime = "5:00pm";
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[start time=").append(startTime).append(",end time=")
				.append(endTime).append("]");
		return sb.toString();
	}
}
