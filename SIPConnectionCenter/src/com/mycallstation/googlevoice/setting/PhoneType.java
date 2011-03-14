/**
 * 
 */
package com.mycallstation.googlevoice.setting;

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

	public static PhoneType getTypeByValue(int value) {
		for (PhoneType type : PhoneType.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		return null;
	}

	public boolean lessThan(int type) {
		switch (this) {
		case HOME:
			return false;
		case MOBILE:
			if (type == 1 || type == 3) {
				return true;
			} else {
				return false;
			}
		case GTALK:
			return true;
		case WORK:
			if (type == 1) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
}
