/**
 * 
 */
package com.sipcm.googlevoice.setting;

/**
 * @author wgao
 * 
 */
public enum PhoneType {
	HOME(1), MOBILE(2), WORK(3), GTALK(9);
	private int value;

	private PhoneType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public PhoneType getTypeByValue(int value) {
		for (PhoneType type : PhoneType.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		return null;
	}
}
