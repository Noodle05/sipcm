package com.mycallstation.sip.vendor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.dataaccess.model.VoipVendor;

public interface VoipVendorContext {

	public void initialize(VoipVendor voipVendor);

	public void registerForIncomingRequest(UserVoipAccount account);

	public void unregisterForIncomingRequest(UserVoipAccount account);

	public boolean handleInvite(SipServletRequest req, String toUser);

	public void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account) throws ServletException, IOException;

	public Address createToAddress(String toAddress, UserVoipAccount account);

	public Address createFromAddress(UserVoipAccount account);
}
