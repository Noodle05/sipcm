/**
 * 
 */
package com.mycallstation.sip.vendor;

import javax.annotation.Resource;
import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * Default voip vendor utility implementation
 * 
 * @author wgao
 */
@Component("defaultVoipVendorUtil")
public class DefaultVoipVendorUtil implements VoipVendorUtil {
	@Resource(name = "voipVendorManager")
	protected VoipVendorManager voipVendorManager;

	/*
	 * Format toAddress to standard phone number. All phone number format into
	 * +xxxxxxxx format.
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorUtil#createToURI(java.lang.String,
	 * com.mycallstation.dataaccess.model.UserVoipAccount)
	 */
	@Override
	public Address createToAddress(String toUser, UserVoipAccount account) {
		URI toURI = voipVendorManager.getSipFactory().createSipURI(
				PhoneNumberUtil.getCanonicalizedPhoneNumber(toUser),
				account.getVoipVendor().getDomain());
		Address toAddress = voipVendorManager.getSipFactory().createAddress(
				toURI);
		return toAddress;
	}

	/*
	 * Format from address. If account has phone number, use phone number as
	 * display name, otherwise, use user's display name. This is for correct
	 * caller id.
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
			fromDisplayName = PhoneNumberUtil
					.getCanonicalizedPhoneNumber(account.getPhoneNumber());
		} else {
			fromDisplayName = displayName;
		}
		Address fromAddress = voipVendorManager.getSipFactory().createAddress(
				fromURI, fromDisplayName);
		return fromAddress;
	}
}
