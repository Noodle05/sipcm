/**
 * 
 */
package com.mycallstation.sip.business;

import java.util.List;

import javax.servlet.sip.Address;

import com.mycallstation.base.business.Service;
import com.mycallstation.sip.model.AddressBinding;
import com.mycallstation.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public interface AddressBindingService extends Service<AddressBinding, Long> {
	public AddressBinding createAddressBindingEntity(
			UserSipProfile userSipProfile, Address address, int expires,
			Address remoteEnd, String callId, boolean takeItOnline);

	public List<AddressBinding> getAddressBindings(UserSipProfile userSipProfile);

	public void removeByUserSipProfile(UserSipProfile userSipProfile);

	public AddressBinding removeBinding(AddressBinding binding,
			UserSipProfile userSipProfile, boolean takeItOffline);
}
