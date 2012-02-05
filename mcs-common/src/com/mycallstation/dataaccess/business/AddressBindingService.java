/**
 * 
 */
package com.mycallstation.dataaccess.business;

import java.util.List;

import com.mycallstation.base.business.Service;
import com.mycallstation.dataaccess.model.AddressBinding;
import com.mycallstation.dataaccess.model.UserSipProfile;

/**
 * @author Wei Gao
 * 
 */
public interface AddressBindingService extends Service<AddressBinding, Long> {
	public AddressBinding createAddressBindingEntity(
			UserSipProfile userSipProfile, String address, int expires,
			String remoteEnd, String callId, boolean takeItOnline);

	public List<AddressBinding> getAddressBindings(UserSipProfile userSipProfile);

	public void removeByUserSipProfile(UserSipProfile userSipProfile);

	public AddressBinding removeBinding(AddressBinding binding,
			UserSipProfile userSipProfile, boolean takeItOffline);
}
