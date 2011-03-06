/**
 * 
 */
package com.sipcm.googlevoice.setting;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wgao
 * 
 */
public class GoogleVoiceConfig implements Serializable {
	private static final long serialVersionUID = 4829353717172161920L;
	public Map<Integer, Phone> phones;
	public int[] phoneList;
	public Settings settings;

	/**
	 * @return the phones
	 */
	public Map<Integer, Phone> getPhones() {
		return phones;
	}

	/**
	 * @param phones
	 *            the phones to set
	 */
	public void setPhones(Map<Integer, Phone> phones) {
		this.phones = phones;
	}

	/**
	 * @return the phoneList
	 */
	public int[] getPhoneList() {
		return phoneList;
	}

	/**
	 * @param phoneList
	 *            the phoneList to set
	 */
	public void setPhoneList(int[] phoneList) {
		this.phoneList = phoneList;
	}

	/**
	 * @return the settings
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @param settings
	 *            the settings to set
	 */
	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
