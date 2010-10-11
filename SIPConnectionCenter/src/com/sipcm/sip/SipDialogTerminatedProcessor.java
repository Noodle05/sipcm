/**
 * 
 */
package com.sipcm.sip;

import javax.sip.DialogTerminatedEvent;

/**
 * @author wgao
 * 
 */
public interface SipDialogTerminatedProcessor {
	public void processDialogTerminated(
			DialogTerminatedEvent dialogTerminatedEvent);
}
