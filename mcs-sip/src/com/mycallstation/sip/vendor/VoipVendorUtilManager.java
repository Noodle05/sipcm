/**
 * 
 */
package com.mycallstation.sip.vendor;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mycallstation.dataaccess.model.VoipVendor;

/**
 * @author wgao
 * 
 */
@Component("voipVendorUtilManager")
public class VoipVendorUtilManager {
	private static final Logger logger = LoggerFactory
			.getLogger(VoipVendorUtilManager.class);

	@Resource(name = "voipVendorUtils")
	private Map<String, VoipVendorUtil> voipVendorUtils;

	@Resource(name = "defaultVoipVendorUtil")
	private VoipVendorUtil defaultVoipVendorUtil;

	public VoipVendorUtil getVoipVendorUtil(VoipVendor vendor) {
		if (vendor == null) {
			throw new NullPointerException("Voip vendor cannot be null.");
		}
		String vendorName = vendor.getName();
		if (logger.isTraceEnabled()) {
			logger.trace("Getting voip vendor util for vendor name \"{}\"",
					vendorName);
		}
		VoipVendorUtil util = voipVendorUtils.get(vendorName);
		if (util == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Voip vendor util not in map, return default one.");
			}
			return defaultVoipVendorUtil;
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Found voip vendor util for \"{}\", return it.",
						vendorName);
			}
			return util;
		}
	}
}
