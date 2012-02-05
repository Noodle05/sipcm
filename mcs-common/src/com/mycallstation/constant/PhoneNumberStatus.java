/**
 * 
 */
package com.mycallstation.constant;

/**
 * @author Wei Gao
 * 
 */
public enum PhoneNumberStatus {
	UNVERIFIED, GOOGLEVOICEVERIFIED, RANDOMNUMBERVERIFIED;

	public boolean isVerified() {
		switch (this) {
		case UNVERIFIED:
			return false;
		default:
			return true;
		}
	}
}
