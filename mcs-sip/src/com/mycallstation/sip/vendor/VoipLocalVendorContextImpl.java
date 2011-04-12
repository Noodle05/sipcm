/**
 * 
 */
package com.mycallstation.sip.vendor;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.model.AddressBinding;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.dataaccess.model.VoipVendor;
import com.mycallstation.sip.locationservice.LocationService;
import com.mycallstation.sip.locationservice.UserBindingInfo;
import com.mycallstation.sip.servlet.AbstractSipServlet;
import com.mycallstation.sip.util.SipConfiguration;

/**
 * @author wgao
 * 
 */
@Component("sipVoipLocalVendorContext")
@Scope("prototype")
public class VoipLocalVendorContextImpl implements VoipVendorContext {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "sipLocationService")
	protected LocationService locationService;

	@Resource(name = "voipVendorManager")
	protected VoipVendorManager voipVendorManager;

	@Resource(name = "systemConfiguration")
	protected SipConfiguration appConfig;

	protected VoipVendor voipVendor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorContext#registerForIncomingRequest
	 * (com .mycallstation.sip.model.UserVoipAccount)
	 */
	@Override
	public void registerForIncomingRequest(UserVoipAccount account) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorContext#unregisterForIncomingRequest
	 * (com .mycallstation.sip.model.UserVoipAccount)
	 */
	@Override
	public void unregisterForIncomingRequest(UserVoipAccount account) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorContext#handleInvite(javax.servlet
	 * .sip.SipServletRequest, java.lang.String)
	 */
	@Override
	public boolean handleInvite(SipServletRequest req, String toUser) {
		UserSipProfile usp = userSipProfileService
				.getUserSipProfileByUsername(toUser);
		if (usp != null) {
			Collection<AddressBinding> abs = locationService
					.getUserBinding(usp);
			if (abs != null && !abs.isEmpty()) {
				UserBindingInfo ubi = new UserBindingInfo(null, abs);
				req.setAttribute(AbstractSipServlet.TARGET_USERSIPBINDING, ubi);
			} else {
				req.setAttribute(AbstractSipServlet.USER_ATTRIBUTE, usp);
			}
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorContext#initialize(com.mycallstation
	 * .sip.model .VoipVendor)
	 */
	@Override
	public void initialize(VoipVendor voipVendor) {
		this.voipVendor = voipVendor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorContext#handleRegisterResponse
	 * (javax.servlet .sip.SipServletResponse,
	 * com.mycallstation.sip.model.UserVoipAccount)
	 */
	@Override
	public void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account) throws ServletException, IOException {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorContext#createToURI(java.lang.
	 * String, com.mycallstation.dataaccess.model.UserVoipAccount)
	 */
	@Override
	public Address createToAddress(String toAddress, UserVoipAccount account) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.vendor.VoipVendorContext#createFromAddress(com.
	 * mycallstation.dataaccess.model.UserVoipAccount)
	 */
	@Override
	public Address createFromAddress(UserVoipAccount account) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Voip vendor context for local domain.";
	}
}
