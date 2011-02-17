/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
@Component("sipVoipVendorContext")
public class VoipVendorContextImpl extends VoipLocalVendorContextImpl {
	@Resource(name = "userVoidAccountService")
	private UserVoipAccountService userVoipAccountService;

	private ConcurrentMap<String, ClientRegisterHolder> cache;

	@PostConstruct
	public void init() {
		// cache = new MapMaker().concurrencyLevel(32).softValues()
		// .expiration(30, TimeUnit.MINUTES).makeMap();
		cache = new ConcurrentHashMap<String, ClientRegisterHolder>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorContext#onUserDeleted(java.lang.Long)
	 */
	@Override
	public void onUserDeleted(Long... userIds) {
		List<Long> ids = Arrays.asList(userIds);
		Collections.sort(ids);
		Iterator<Entry<String, ClientRegisterHolder>> ite = cache.entrySet()
				.iterator();
		while (ite.hasNext() && !ids.isEmpty()) {
			Entry<String, ClientRegisterHolder> entry = ite.next();
			ClientRegisterHolder holder = entry.getValue();
			UserSipProfile userSipProfile = holder.getUserSipProfile();
			int index = Collections.binarySearch(ids, userSipProfile.getId());
			if (index >= 0) {
				ids.remove(index);
				ite.remove();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#registerForIncomingRequest(com
	 * .sipcm.sip.model.UserSipProfile, com.sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void registerForIncomingRequest(UserSipProfile userSipProfile,
			UserVoipAccount account) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorContext#unregisterForIncomingRequest(com
	 * .sipcm.sip.model.UserSipProfile, com.sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void unregisterForIncomingRequest(UserSipProfile userSipProfile,
			UserVoipAccount account) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorContext#isLocalUser(java.lang.String)
	 */
	@Override
	public Collection<AddressBinding> isLocalUser(String toUser) {
		UserVoipAccount account = userVoipAccountService
				.getUserVoipAccountByVendorAndAccount(voipVender, toUser);
		if (account != null) {
			UserSipProfile profile = account.getOwner();
			return locationService.getUserBinding(profile);
		}
		return null;
	}
}
