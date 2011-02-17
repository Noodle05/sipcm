/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.sip.business.UserSipProfileService;
import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Component("sipVoipLocalVendorContext")
public class VoipLocalVendorContextImpl implements VoipVendorContext {
	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "sipLocationService")
	protected LocationService locationService;

	protected VoipVendor voipVender;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#onUserDeleted(java.lang.Long[])
	 */
	@Override
	public void onUserDeleted(Long... userIds) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#registerForIncomingRequest(com
	 * .sipcm.sip.model.UserSipProfile, com.sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void registerForIncomingRequest(UserSipProfile userSipProfile,
			UserVoipAccount account) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#unregisterForIncomingRequest(com
	 * .sipcm.sip.model.UserSipProfile, com.sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void unregisterForIncomingRequest(UserSipProfile userSipProfile,
			UserVoipAccount account) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorContext#isLocalUser(java.lang.String)
	 */
	@Override
	public Collection<AddressBinding> isLocalUser(String toUser) {
		UserSipProfile usp = userSipProfileService
				.getUserSipProfileByUsername(toUser);
		if (usp != null) {
			return locationService.getUserSipBindingBySipProfile(usp);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#setVoipVendor(com.sipcm.sip.model
	 * .VoipVendor)
	 */
	@Override
	public void setVoipVendor(VoipVendor voipVendor) {
		this.voipVender = voipVendor;
	}
}
