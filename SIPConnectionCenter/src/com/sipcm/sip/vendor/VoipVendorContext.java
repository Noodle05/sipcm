/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.google.common.collect.MapMaker;
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

	private ConcurrentMap<String, UserSipProfile> cache;

	public void setVoipVendor(VoipVendor voipVendor) {
		this.voipVender = voipVendor;
	}

	@PostConstruct
	public void init() {
		cache = new MapMaker().concurrencyLevel(32).softValues()
				.expiration(30, TimeUnit.MINUTES).makeMap();
	}

	public UserSipProfile getUserSipProfileByAccount(String account) {
		UserSipProfile ret = cache.get(account);
		if (ret == null) {
			UserVoipAccount uva = userVoipAccountService
					.getUserVoipAccountByVendorAndAccount(voipVender, account);
			if (uva != null) {
				ret = uva.getOwner();
				if (ret != null) {
					UserSipProfile tmp = cache.putIfAbsent(account, ret);
					if (tmp != null) {
						ret = tmp;
					}
				}
			}
		}
		return ret;
	}

	public void onUserDeleted(Long... userIds) {
		List<Long> ids = Arrays.asList(userIds);
		Collections.sort(ids);
		Iterator<Entry<String, UserSipProfile>> ite = cache.entrySet()
				.iterator();
		while (ite.hasNext() && !ids.isEmpty()) {
			Entry<String, UserSipProfile> entry = ite.next();
			UserSipProfile userSipProfile = entry.getValue();
			int index = Collections.binarySearch(ids, userSipProfile.getId());
			if (index >= 0) {
				ids.remove(index);
				ite.remove();
			}
		}
	}
}
