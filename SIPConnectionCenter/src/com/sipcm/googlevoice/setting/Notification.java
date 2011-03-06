/**
 * 
 */
package com.sipcm.googlevoice.setting;

import java.io.Serializable;

/**
 * @author wgao
 * 
 */
public class Notification implements Serializable {
	private static final long serialVersionUID = -8877201208509912016L;
	public String address;
	public boolean active;

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
