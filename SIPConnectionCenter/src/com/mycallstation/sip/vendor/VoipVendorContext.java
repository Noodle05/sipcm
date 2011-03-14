package com.mycallstation.sip.vendor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletResponse;

import com.mycallstation.sip.locationservice.UserBindingInfo;
import com.mycallstation.sip.model.UserVoipAccount;
import com.mycallstation.sip.model.VoipVendor;

public interface VoipVendorContext {

	public void initialize(VoipVendor voipVendor);

	public void registerForIncomingRequest(UserVoipAccount account);

	public void unregisterForIncomingRequest(UserVoipAccount account);

	public UserBindingInfo isLocalUser(String toUser);

	public void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account) throws ServletException, IOException;
}
