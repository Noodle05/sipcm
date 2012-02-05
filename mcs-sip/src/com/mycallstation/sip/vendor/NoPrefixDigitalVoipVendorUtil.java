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
 * @author Wei Gao
 * 
 */
@Component("noprefix.digital.voipVendorUtil")
public class NoPrefixDigitalVoipVendorUtil extends DefaultVoipVendorUtil {
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
				PhoneNumberUtil.getNoPrefixDigitalPhoneNumber(toUser),
				account.getVoipVendor().getDomain());
		Address toAddress = voipVendorManager.getSipFactory().createAddress(
				toURI);
		return toAddress;
	}
}
