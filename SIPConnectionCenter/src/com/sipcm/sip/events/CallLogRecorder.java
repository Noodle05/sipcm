/**
 * 
 */
package com.sipcm.sip.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sip.CallLogRecorder")
public class CallLogRecorder implements CallEventListener {
	private static final Logger logger = LoggerFactory
			.getLogger(CallLogRecorder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallStart(com.sipcm.sip
	 * .events.CallStartEvent)
	 */
	@Override
	public void outgoingCallStart(CallStartEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Outgoing call start: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallStart(com.sipcm.sip
	 * .events.CallStartEvent)
	 */
	@Override
	public void incomingCallStart(CallStartEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Incoming call start: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallEstablished(com.sipcm
	 * .sip.events.CallStartEvent)
	 */
	@Override
	public void outgoingCallEstablished(CallStartEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Outgoing call established: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallEstablished(com.sipcm
	 * .sip.events.CallStartEvent)
	 */
	@Override
	public void incomingCallEstablished(CallStartEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Incoming call established: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallEnd(com.sipcm.sip.
	 * events.CallEndEvent)
	 */
	@Override
	public void outgoingCallEnd(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Outgoing call end: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallEnd(com.sipcm.sip.
	 * events.CallEndEvent)
	 */
	@Override
	public void incomingCallEnd(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Incoming call end: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallFailed(com.sipcm.sip
	 * .events.CallEndEvent)
	 */
	@Override
	public void outgoingCallFailed(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Outgoing call failed: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallFailed(com.sipcm.sip
	 * .events.CallEndEvent)
	 */
	@Override
	public void incomingCallFailed(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Incoming call failed: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#outgoingCallCancelled(com.sipcm
	 * .sip.events.CallEndEvent)
	 */
	@Override
	public void outgoingCallCancelled(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Outgoing call cancelled: \"{}\"", event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.CallEventListener#incomingCallCancelled(com.sipcm
	 * .sip.events.CallEndEvent)
	 */
	@Override
	public void incomingCallCancelled(CallEndEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("Incoming call cancelled: \"{}\"", event);
		}
	}
}
