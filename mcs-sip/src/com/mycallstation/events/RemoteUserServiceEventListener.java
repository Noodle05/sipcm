/**
 * 
 */
package com.mycallstation.events;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mycallstation.sip.locationservice.LocationService;
import com.mycallstation.sip.vendor.VoipVendorManager;

/**
 * @author wgao
 * 
 */
@Component("remoteUserServiceEventListener")
public class RemoteUserServiceEventListener implements MessageDelegate {
	private static final Logger logger = LoggerFactory
			.getLogger(RemoteUserServiceEventListener.class);
	@Resource(name = "sipLocationService")
	private LocationService locationService;

	@Resource(name = "voipVendorManager")
	private VoipVendorManager voipVendorManager;

	@Override
	public void handleMessage(Serializable message) {
		if (message instanceof ServiceEvent) {
			if (logger.isTraceEnabled()) {
				logger.trace(
						"Remote user service event listener get event: \"{}\"",
						message);
			}
			final ServiceEvent event = (ServiceEvent) message;
			switch (event.getOperation()) {
			case CREATED:
				onUserCreated(event.getIds());
				break;
			case MODIFIED:
				onUserModified(event.getIds());
				break;
			case DELETED:
				onUserDeleted(event.getIds());
				break;
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("Object in message must be type of ServiceEvent.");
			}
		}
	}

	@Override
	public void handleMessage(String message) {
		logError(message);
	}

	@Override
	public void handleMessage(Map<?, ?> message) {
		logError(message);
	}

	@Override
	public void handleMessage(byte[] message) {
		logError(message);
	}

	private void logError(Object message) {
		if (logger.isWarnEnabled()) {
			logger.warn("Message must be type of ObjectMessage");
		}
	}

	private void onUserCreated(Long[] ids) {
		// Do nothing
	}

	private void onUserModified(Long[] ids) {
		locationService.onUserChanged(ids);
	}

	private void onUserDeleted(Long[] ids) {
		locationService.onUserChanged(ids);
		voipVendorManager.onUserDeleted(ids);
	}
}
