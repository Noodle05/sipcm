/**
 * 
 */
package com.sipcm.sip.vendor;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletResponse;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "sipLocationService")
	protected LocationService locationService;

	@Resource(name = "voipVendorManager")
	protected VoipVendorManager voipVendorManager;

	@Resource(name = "applicationConfiguration")
	protected Configuration appConfig;

	protected VoipVendor voipVendor;

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
	 * .sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void registerForIncomingRequest(UserVoipAccount account) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#unregisterForIncomingRequest(com
	 * .sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void unregisterForIncomingRequest(UserVoipAccount account) {
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
			return locationService.getUserBinding(usp);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#initialize(com.sipcm.sip.model
	 * .VoipVendor)
	 */
	@Override
	public void initialize(VoipVendor voipVendor) {
		this.voipVendor = voipVendor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#handleRegisterResponse(javax.servlet
	 * .sip.SipServletResponse, com.sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account) throws ServletException, IOException {
		// Do nothing
	}
}
