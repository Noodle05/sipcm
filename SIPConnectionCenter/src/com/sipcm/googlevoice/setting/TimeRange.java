/**
 * 
 */
package com.sipcm.googlevoice.setting;

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
}
