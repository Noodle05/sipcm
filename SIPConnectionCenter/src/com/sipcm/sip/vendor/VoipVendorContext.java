/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.Arrays;
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
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Component("sipVoipVendorContext")
public class VoipVendorContext {
	@Resource(name = "userVoidAccountService")
	private UserVoipAccountService userVoipAccountService;

	private VoipVendor voipVender;

	private ConcurrentMap<String, ClientRegisterHolder> cache;

	public void setVoipVendor(VoipVendor voipVendor) {
		this.voipVender = voipVendor;
	}

	@PostConstruct
	public void init() {
		// cache = new MapMaker().concurrencyLevel(32).softValues()
		// .expiration(30, TimeUnit.MINUTES).makeMap();
		cache = new ConcurrentHashMap<String, ClientRegisterHolder>();
	}

	public UserSipProfile getUserSipProfileByAccount(String account) {
		ClientRegisterHolder holder = cache.get(account);
		UserSipProfile ret = holder.getUserSipProfile();
		// if (ret == null) {
		// UserVoipAccount uva = userVoipAccountService
		// .getUserVoipAccountByVendorAndAccount(voipVender, account);
		// if (uva != null) {
		// ret = uva.getOwner();
		// if (ret != null) {
		// UserSipProfile tmp = cache.putIfAbsent(account, ret);
		// if (tmp != null) {
		// ret = tmp;
		// }
		// }
		// }
		// }
		return ret;
	}

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

	public void registerForIncomingRequest(UserSipProfile userSipProfile,
			UserVoipAccount account) {
		  
	}

	public void unregisterForIncomingRequest(UserSipProfile userSipProfile,
			UserVoipAccount account) {
		// TODO Auto-generated method stub

	}
}
