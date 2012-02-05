/**
 * 
 */
package com.mycallstation.sip.vendor;

import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * @author Wei Gao
 * 
 */
@Component("sipgate.voipVendorUtil")
public class SipGateVoipVendorUtil extends DefaultVoipVendorUtil {
	@Override
	public Address createFromAddress(UserVoipAccount account) {
		URI fromURI = voipVendorManager.getSipFactory().createSipURI(
				account.getAccount(), account.getVoipVendor().getDomain());
		if (account.getOwner().isCallAnonymously()) {
			return voipVendorManager.getSipFactory().createAddress(fromURI);
		} else {
			return voipVendorManager.getSipFactory().createAddress(fromURI,
					account.getOwner().getDisplayName());
		}
	}
}
