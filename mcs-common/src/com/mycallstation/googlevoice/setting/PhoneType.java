/**
 * 
 */
package com.mycallstation.googlevoice.setting;

/**
 * @author Wei Gao
 * 
 */
public enum PhoneType {
	HOME(1, 100), MOBILE(2, 50), WORK(3, 75), GTALK(9, 0);
	private final int value;
	private final int weight;

	private PhoneType(int value, int weight) {
		this.value = value;
		this.weight = weight;
	}

	public int getValue() {
		return value;
	}

	public int getWeight() {
		return weight;
	}

	public static PhoneType valueOf(int value) {
		for (PhoneType type : PhoneType.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		return HOME;
	}

	public boolean lessThan(PhoneType type) {
		return weight - type.getWeight() < 0;
	}
}
