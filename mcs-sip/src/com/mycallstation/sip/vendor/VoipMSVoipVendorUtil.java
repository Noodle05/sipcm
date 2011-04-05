/**
 * 
 */
package com.mycallstation.sip.vendor;

import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * VoIP vendor utility implementation for voip.ms
 * 
 * @author wgao
 */
@Component("voip.ms.voipVendorUtil")
public class VoipMSVoipVendorUtil extends DefaultVoipVendorUtil {

	/*
	 * voip.ms use phone number format 011xxxxxxx
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorUtil#createToURI(java.lang.String,
	 * com.mycallstation.dataaccess.model.UserVoipAccount)
	 */
	@Override
	public Address createToAddress(String toUser, UserVoipAccount account) {
		URI toURI = voipVendorManager.getSipFactory().createSipURI(
				PhoneNumberUtil.getDigitalPhoneNumber(toUser),
				account.getVoipVendor().getDomain());
		Address toAddress = voipVendorManager.getSipFactory().createAddress(
				toURI);
		return toAddress;
	}

	/*
	 * Format from display name as 011xxxxxxx too
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorUtil#createFromURI(com.mycallstation
	 * .dataaccess.model.UserVoipAccount)
	 */
	@Override
	public Address createFromAddress(String displayName, UserVoipAccount account) {
		URI fromURI = voipVendorManager.getSipFactory().createSipURI(
				account.getAccount(), account.getVoipVendor().getDomain());
		String fromDisplayName;
		if (account.getPhoneNumber() != null) {
			fromDisplayName = PhoneNumberUtil.getDigitalPhoneNumber(account
					.getPhoneNumber());
		} else {
			fromDisplayName = displayName;
		}
		Address fromAddress = voipVendorManager.getSipFactory().createAddress(
				fromURI, fromDisplayName);
		return fromAddress;
	}
}
