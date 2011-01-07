/**
 * 
 */
package com.sipcm.sip.vendor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.sip.business.VoipVendorService;
import com.sipcm.sip.model.VoipVendor;

/**
 * @author wgao
 * 
 */
public abstract class VoipVendorManager {
	private static final Logger logger = LoggerFactory
			.getLogger(VoipVendorManager.class);

	private ConcurrentMap<VoipVendor, VoipVendorContext> voipVendors;

	@Resource(name = "voipVendorService")
	private VoipVendorService voipVendorService;

	protected abstract VoipVendorContext createSipVoipVendorContext();

	private VoipVendorContext createVoipVendorContext(VoipVendor voipVendor) {
		VoipVendorContext ctx = null;
		try {
			switch (voipVendor.getType()) {
			case SIP:
				ctx = createSipVoipVendorContext();
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
		voipVendors = new ConcurrentHashMap<VoipVendor, VoipVendorContext>();
		List<VoipVendor> venders = voipVendorService.getEntities();
		for (VoipVendor vender : venders) {
			VoipVendorContext ctx = createVoipVendorContext(vender);
			if (ctx != null) {
				voipVendors.put(vender, ctx);
			}
		}
	}

	public void onUserDeleted(Long... userIds) {
		for (VoipVendorContext ctx : voipVendors.values()) {
			ctx.onUserDeleted(userIds);
		}
	}
}
