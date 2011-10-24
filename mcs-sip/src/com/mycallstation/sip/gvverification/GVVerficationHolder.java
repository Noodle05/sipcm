/**
 * 
 */
package com.mycallstation.sip.gvverification;

import java.io.Serializable;

import com.mycallstation.googlevoice.GoogleVoiceSession;
import com.mycallstation.googlevoice.setting.Phone;

/**
 * @author wgao
 * 
 */
class GVVerficationHolder implements Serializable {
	private static final long serialVersionUID = -5037914594629241450L;

	private final GoogleVoiceSession gvSession;
	private final String gvNumber;
	private final Phone phone;
	private final int verifyCode;

	GVVerficationHolder(GoogleVoiceSession gvSession, String gvNumber, Phone phone,
			int verifyCode) {
		this.gvSession = gvSession;
		this.gvNumber = gvNumber;
		this.phone = phone;
		this.verifyCode = verifyCode;
	}

	/**
	 * @return the gvSession
	 */
	public GoogleVoiceSession getGvSession() {
		return gvSession;
	}

	/**
	 * @return the gvNumber
	 */
	public String getGvNumber() {
		return gvNumber;
	}

	/**
	 * @return the phone
	 */
	public Phone getPhone() {
		return phone;
	}

	/**
	 * @return the verifyCode
	 */
	public int getVerifyCode() {
		return verifyCode;
	}
}
