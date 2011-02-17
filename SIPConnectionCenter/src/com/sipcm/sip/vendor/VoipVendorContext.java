package com.sipcm.sip.vendor;

import java.util.Collection;

import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;

public interface VoipVendorContext {

	public void setVoipVendor(VoipVendor voipVendor);

	public void onUserDeleted(Long... userIds);

	public void registerForIncomingRequest(UserSipProfile userSipProfile,
			UserVoipAccount account);

	public void unregisterForIncomingRequest(UserSipProfile userSipProfile,
			UserVoipAccount account);

	public Collection<AddressBinding> isLocalUser(String toUser);

}