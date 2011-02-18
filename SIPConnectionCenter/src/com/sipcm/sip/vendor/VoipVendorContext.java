package com.sipcm.sip.vendor;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletResponse;

import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;

public interface VoipVendorContext {

	public void initialize(VoipVendor voipVendor);

	public void registerForIncomingRequest(UserVoipAccount account);

	public void unregisterForIncomingRequest(UserVoipAccount account);

	public Collection<AddressBinding> isLocalUser(String toUser);

	public void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account) throws ServletException, IOException;
}