package com.mycallstation.sip.vendor;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;

public interface VoipVendorManager {

	public void registerForIncomingRequest(UserSipProfile userSipProfile);

	public void unregisterForIncomingRequest(UserSipProfile userSipProfile);

	public void renewForIncomingRequest(UserSipProfile userSipProfile);

	public void onUserDeleted(Long... userIds);

	public boolean handleInvite(SipServletRequest req, String toHost,
			String toUser);

	public SipFactory getSipFactory();

	public List<String> getSupportedMethods();

	public String getContactHost();

	public void setListeningAddress(InetAddress listeningIp, int listeningPort);

	public void handleRegisterResponse(SipServletResponse resp)
			throws ServletException, IOException;

	public void registerClientRenew();

	public Address createToAddress(String toAddress, UserVoipAccount account);

	public Address createFromAddress(UserVoipAccount account);
}
