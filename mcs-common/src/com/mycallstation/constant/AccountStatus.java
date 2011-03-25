/**
 * 
 */
package com.mycallstation.constant;

/**
 * @author wgao
 * 
 */
public enum AccountStatus {
	PENDING, ACTIVE, DISABLED;

	public boolean isActive() {
		switch (this) {
		case ACTIVE:
			return true;
		default:
			return false;
		}
	}
}
