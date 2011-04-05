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
 * nonoh.net VoIP vendor utility implementation
 * 
 * @author wgao
 */
@Component("nonoh.net.voipVendorUtil")
public class NonohVoipVendorUtil extends DefaultVoipVendorUtil {
	/*
	 * nonoh.net use 1xxxxxxxxx as from address for callerid.
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.DefaultVoipVendorUtil#createFromAddress(
	 * java.lang.String, com.mycallstation.dataaccess.model.UserVoipAccount)
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
