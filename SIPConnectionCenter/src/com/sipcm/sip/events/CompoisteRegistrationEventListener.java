/**
 * 
 */
package com.sipcm.sip.events;

import java.util.Collection;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sipRegistrationEventListener")
public class CompoisteRegistrationEventListener implements
		RegistrationEventListener {
	private static final Logger logger = LoggerFactory
			.getLogger(CompoisteRegistrationEventListener.class);

	@Resource(name = "sipRegistrationEventListeners")
	private Collection<RegistrationEventListener> listeners;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.RegistrationEventListener#userRegistered
	 * (com.sipcm.sip.locationservice.RegistrationEventObject)
	 */
	@Override
	@Async
	public void userRegistered(RegistrationEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (RegistrationEventListener listener : listeners) {
				try {
					listener.userRegistered(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" on registration event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.locationservice.RegistrationEventListener#userUnregistered
	 * (com.sipcm.sip.locationservice.RegistrationEventObject)
	 */
	@Override
	@Async
	public void userUnregistered(RegistrationEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (RegistrationEventListener listener : listeners) {
				try {
					listener.userUnregistered(event);
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error(
								"Error happened when notify listener \"{}\" on unregistration event: \"{}\", check debug log for exception stack.",
								listener, event);
						if (logger.isDebugEnabled()) {
							logger.debug("Detail exception stack:", e);
						}
					}
				}
			}
		}
	}
}
