package com.sipcm.sip.vendor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletResponse;

import com.sipcm.sip.locationservice.UserBindingInfo;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;

public interface VoipVendorContext {

	public void initialize(VoipVendor voipVendor);

	public void registerForIncomingRequest(UserVoipAccount account);

	public void unregisterForIncomingRequest(UserVoipAccount account);

	public UserBindingInfo isLocalUser(String toUser);

	public void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account) throws ServletException, IOException;
}