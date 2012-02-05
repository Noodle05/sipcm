/**
 * 
 */
package com.mycallstation.sip.events;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mycallstation.constant.VoipVendorType;
import com.mycallstation.dataaccess.business.UserSipProfileService;

/**
 * @author Wei Gao
 * 
 */
@Component("sipLastReceivedCallUpdator")
public class LastReceivedCallUpdator implements CallEventListener {
	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Override
	public void outgoingCallStart(CallStartEvent event) {
	}

	@Override
	public void incomingCallStart(CallStartEvent event) {
	}

	@Override
	public void outgoingCallEstablished(CallStartEvent event) {
		if (VoipVendorType.GOOGLE_VOICE.equals(event.getAccount()
				.getVoipVendor().getType())) {
			userSipProfileService.updateLastReceiveCallTime(event
					.getUserSipProfile());
		}
	}

	@Override
	public void incomingCallEstablished(CallStartEvent event) {
		if (!event.isFromLocal()) {
			userSipProfileService.updateLastReceiveCallTime(event
					.getUserSipProfile());
		}
	}

	@Override
	public void outgoingCallEnd(CallEndEvent event) {
	}

	@Override
	public void incomingCallEnd(CallEndEvent event) {
	}

	@Override
	public void outgoingCallFailed(CallEndEvent event) {
	}

	@Override
	public void incomingCallFailed(CallEndEvent event) {
	}

	@Override
	public void outgoingCallCancelled(CallEndEvent event) {
	}

	@Override
	public void incomingCallCancelled(CallEndEvent event) {
	}
}
