/**
 * 
 */
package com.sipcm.sip.business;

import java.util.Collection;

import javax.servlet.sip.Address;

import com.sipcm.base.business.Service;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipBinding;

/**
 * @author wgao
 * 
 */
public interface UserSipBindingService extends Service<UserSipBinding, Long> {
	public UserSipBinding removeByAddress(String address);

	public UserSipBinding getUserSipBindingByAddress(String address);

	public Collection<UserSipBinding> checkContactExpires();

	public AddressBinding createAddressBindingEntity(
			UserSipBinding userSipBinding, Address address, Address remoteEnd,
			String callId);
}
