package com.sipcm.sip.vendor;

import java.util.Collection;

import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

public interface VoipVendorManager {

	public void registerForIncomingRequest(UserSipProfile userSipProfile,
			Collection<UserVoipAccount> accounts);

	public void unregisterForIncomingRequest(UserSipProfile userSipProfile,
			Collection<UserVoipAccount> accounts);

	public void onUserDeleted(Long... userIds);

	public Collection<AddressBinding> isLocalUsr(String toHost, String toUser);

}