/**
 * 
 */
package com.mycallstation.googlevoice.setting;

import java.io.Serializable;

/**
 * @author wgao
 * 
 */
public class TimeRange implements Serializable {
	private static final long serialVersionUID = 7935981654988757449L;
	private boolean allDay;
	private Time[] times;

	/**
	 * @return the allDay
	 */
	public boolean isAllDay() {
		return allDay;
	}

	/**
	 * @param allDay
	 *            the allDay to set
	 */
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	/**
	 * @return the times
	 */
	public Time[] getTimes() {
		return times;
	}

	/**
	 * @param times
	 *            the times to set
	 */
	public void setTimes(Time[] times) {
		this.times = times;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[allDay=").append(allDay);
		if (times != null) {
			for (int i = 0; i < times.length; i++) {
				sb.append(",").append(i).append("=").append(times[i]);
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
