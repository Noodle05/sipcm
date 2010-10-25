/**
 * 
 */
package com.sipcm.sip.locationservice;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("contactExpiresTask")
public class ContactExpiresTask {
	private static final Logger logger = LoggerFactory
			.getLogger(ContactExpiresTask.class);

	@Resource(name = "sipLocationService")
	private LocationService locationService;

	public void onCheckExpire() {
		if (logger.isDebugEnabled()) {
			logger.debug("Timeout, check contacts expire.");
		}
		locationService.checkContactExpires();
	}
}
