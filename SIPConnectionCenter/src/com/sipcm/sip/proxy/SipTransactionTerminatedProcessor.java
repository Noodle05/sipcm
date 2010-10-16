/**
 * 
 */
package com.sipcm.sip.proxy;

import javax.sip.TransactionTerminatedEvent;

/**
 * @author wgao
 * 
 */
public interface SipTransactionTerminatedProcessor {
	public void processTransactionTerminated(
			TransactionTerminatedEvent transactionTerminatedEvent);
}
