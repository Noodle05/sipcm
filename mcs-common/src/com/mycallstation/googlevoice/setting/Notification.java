/**
 * 
 */
package com.mycallstation.googlevoice.setting;

import java.io.Serializable;

/**
 * @author Wei Gao
 * 
 */
public class Notification implements Serializable {
	private static final long serialVersionUID = -8877201208509912016L;
	private String address;
	private boolean active;

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
}
