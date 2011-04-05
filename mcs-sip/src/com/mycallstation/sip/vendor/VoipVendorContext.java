package com.mycallstation.sip.vendor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletResponse;

import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.dataaccess.model.VoipVendor;
import com.mycallstation.sip.locationservice.UserBindingInfo;

public interface VoipVendorContext {

	public void initialize(VoipVendor voipVendor);

	public void registerForIncomingRequest(UserVoipAccount account);

	public void unregisterForIncomingRequest(UserVoipAccount account);

	public UserBindingInfo isLocalUser(String toUser);

	public void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account) throws ServletException, IOException;

	public Address createToAddress(String toAddress, UserVoipAccount account);

	public Address createFromAddress(String displayName, UserVoipAccount account);
}
