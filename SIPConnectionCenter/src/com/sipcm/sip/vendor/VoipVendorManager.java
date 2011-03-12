package com.sipcm.sip.vendor;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletResponse;

import com.sipcm.sip.locationservice.UserBindingInfo;
import com.sipcm.sip.model.UserSipProfile;

public interface VoipVendorManager {

	public void registerForIncomingRequest(UserSipProfile userSipProfile);

	public void unregisterForIncomingRequest(UserSipProfile userSipProfile);

	public void renewForIncomingRequest(UserSipProfile userSipProfile);

	public void onUserDeleted(Long... userIds);

	public UserBindingInfo isLocalUsr(String toHost, String toUser);

	public SipFactory getSipFactory();

	public List<String> getSupportedMethods();

	public String getContactHost();

	public void setListeningAddress(InetAddress listeningIp, int listeningPort);

	public void handleRegisterResponse(SipServletResponse resp)
			throws ServletException, IOException;

	public void registerClientRenew();
}