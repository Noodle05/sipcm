/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.sip.business.VoipVendorService;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
public abstract class VoipVendorManagerImpl implements VoipVendorManager {
	private static final Logger logger = LoggerFactory
			.getLogger(VoipVendorManagerImpl.class);

	private final ConcurrentMap<VoipVendor, VoipVendorContext> voipVendors;

	@Resource(name = "voipVendorService")
	private VoipVendorService voipVendorService;

	public VoipVendorManagerImpl() {
		voipVendors = new ConcurrentHashMap<VoipVendor, VoipVendorContext>();
	}

	protected abstract VoipVendorContext createSipVoipVendorContext();

	protected abstract VoipVendorContext createLocalVoipVendorContext();

	private VoipVendorContext createVoipVendorContext(VoipVendor voipVendor) {
		VoipVendorContext ctx = null;
		try {
			switch (voipVendor.getType()) {
			case SIP:
				ctx = createSipVoipVendorContext();
			case LOCAL:
				ctx = createLocalVoipVendorContext();
			}
			if (ctx != null) {
				ctx.setVoipVendor(voipVendor);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"Error happened when creating context object for voip vendor: "
								+ voipVendor, e);
			}
			ctx = null;
		}
		return ctx;
	}

	@PostConstruct
	public void init() {
		List<VoipVendor> venders = voipVendorService.getEntities();
		for (VoipVendor vender : venders) {
			VoipVendorContext ctx = createVoipVendorContext(vender);
			if (ctx != null) {
				voipVendors.put(vender, ctx);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorManager#registerForIncomingRequest(com
	 * .sipcm.sip.model.UserSipProfile, java.util.Collection)
	 */
	@Override
	public void registerForIncomingRequest(UserSipProfile userSipProfile,
			Collection<UserVoipAccount> accounts) {
		for (UserVoipAccount account : accounts) {
			VoipVendorContext ctx = getVoipVendorContext(account);
			if (ctx != null) {
				ctx.registerForIncomingRequest(userSipProfile, account);
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Cannot find vendor context for vendor \"{}\"",
							account.getVoipVendor());
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorManager#unregisterForIncomingRequest(com
	 * .sipcm.sip.model.UserSipProfile, java.util.Collection)
	 */
	@Override
	public void unregisterForIncomingRequest(UserSipProfile userSipProfile,
			Collection<UserVoipAccount> accounts) {
		for (UserVoipAccount account : accounts) {
			VoipVendorContext ctx = getVoipVendorContext(account);
			if (ctx != null) {
				ctx.unregisterForIncomingRequest(userSipProfile, account);
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Cannot find vendor context for vendor \"{}\"",
							account.getVoipVendor());
				}
			}
		}
	}

	private VoipVendorContext getVoipVendorContext(UserVoipAccount account) {
		return voipVendors.get(account.getVoipVendor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorManager#onUserDeleted(java.lang.Long)
	 */
	@Override
	public void onUserDeleted(Long... userIds) {
		for (VoipVendorContext ctx : voipVendors.values()) {
			ctx.onUserDeleted(userIds);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorManager#isLocalUsr(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Collection<AddressBinding> isLocalUsr(String toHost, String toUser) {
		for (Entry<VoipVendor, VoipVendorContext> entry : voipVendors
				.entrySet()) {
			VoipVendor vendor = entry.getKey();
			if (toHost.toUpperCase().endsWith(vendor.getDomain().toUpperCase())) {
				VoipVendorContext ctx = entry.getValue();
				return ctx.isLocalUser(toUser);
			}
		}
		return null;
	}
}
