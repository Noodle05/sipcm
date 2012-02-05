/**
 * 
 */
package com.mycallstation.sip.events;

import java.util.EventListener;

/**
 * @author Wei Gao
 * 
 */
public interface CallEventListener extends EventListener {
	public void outgoingCallStart(CallStartEvent event);

	public void incomingCallStart(CallStartEvent event);

	public void outgoingCallEstablished(CallStartEvent event);

	public void incomingCallEstablished(CallStartEvent event);

	public void outgoingCallEnd(CallEndEvent event);

	public void incomingCallEnd(CallEndEvent event);

	public void outgoingCallFailed(CallEndEvent event);

	public void incomingCallFailed(CallEndEvent event);

	public void outgoingCallCancelled(CallEndEvent event);

	public void incomingCallCancelled(CallEndEvent event);
}
