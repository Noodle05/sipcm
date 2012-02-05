/**
 * 
 */
package com.mycallstation.googlevoice.result;

import com.mycallstation.googlevoice.setting.Phone;

/**
 * @author Wei Gao
 * 
 */
public class PhoneResult extends Phone {
	private static final long serialVersionUID = 3582874256392501559L;

	private String forwardingTakenMessage;

	/**
	 * @return the forwardingTakenMessage
	 */
	public String getForwardingTakenMessage() {
		return forwardingTakenMessage;
	}

	/**
	 * @param forwardingTakenMessage
	 *            the forwardingTakenMessage to set
	 */
	public void setForwardingTakenMessage(String forwardingTakenMessage) {
		this.forwardingTakenMessage = forwardingTakenMessage;
	}
}
