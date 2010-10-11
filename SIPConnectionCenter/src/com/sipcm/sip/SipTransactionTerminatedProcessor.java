/**
 * 
 */
package com.sipcm.sip;

import javax.sip.TransactionTerminatedEvent;

/**
 * @author wgao
 * 
 */
public interface SipTransactionTerminatedProcessor {
	public void processTransactionTerminated(
			TransactionTerminatedEvent transactionTerminatedEvent);
}
