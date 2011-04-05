/**
 * 
 */
package com.mycallstation.sip.vendor;

import javax.servlet.sip.Address;

import com.mycallstation.dataaccess.model.UserVoipAccount;

/**
 * Utility class for voip vendors. Help to generate to address and from address.
 * 
 * @author wgao
 */
public interface VoipVendorUtil {
	/**
	 * Generate toAddress for this VoIP vendor
	 * 
	 * @param toAddress
	 * @param account
	 * @return
	 */
	public Address createToAddress(String toAddress, UserVoipAccount account);

	/**
	 * Generate fromAddress for this VoIP vendor
	 * 
	 * @param displayName
	 * @param account
	 * @return
	 */
	public Address createFromAddress(String displayName, UserVoipAccount account);
}
