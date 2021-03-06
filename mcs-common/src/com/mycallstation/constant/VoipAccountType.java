/**
 * 
 */
package com.mycallstation.constant;

/**
 * @author Wei Gao
 * 
 */
public enum VoipAccountType {
	INCOME("Income"), OUTGOING("Outgoing"), BOTH("Both");

	private String label;

	private VoipAccountType(String lable) {
		this.label = lable;
	}

	public String getLabel() {
		return label;
	}
}
