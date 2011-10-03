/**
 * 
 */
package com.mycallstation.googlevoice.setting;

/**
 * @author wgao
 * 
 */
public enum ScheduleSet {
	FALSE("false"), TRUE("true"), ONE("1");

	private final String value;

	private ScheduleSet(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static ScheduleSet byValue(String value) {
		for (ScheduleSet scheduleSet : values()) {
			if (scheduleSet.getValue().equalsIgnoreCase(value)) {
				return scheduleSet;
			}
		}
		return FALSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}
