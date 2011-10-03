/**
 * 
 */
package com.mycallstation.googlevoice.setting;

/**
 * @author wgao
 * 
 */
public enum VoiceMailAccessPolicy {
	NO(0, "no"), YES_WITH_PIN(1, "yes with pin"), YES_WITHOUT_PIN(3,
			"yes without pin");

	private final int value;
	private final String string;

	private VoiceMailAccessPolicy(int value, String string) {
		this.value = value;
		this.string = string;
	}

	public int getValue() {
		return value;
	}

	public static VoiceMailAccessPolicy valueOf(int value) {
		for (VoiceMailAccessPolicy policy : values()) {
			if (policy.getValue() == value) {
				return policy;
			}
		}
		return NO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return string;
	}
}
