/**
 * 
 */
package com.sipcm.sip;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sipServerMessageProcessor")
public class SipServerMessageProcesser implements SipRequestProcessor,
		SipResponseProcessor, SipTimeoutProcessor, SipIOExceptionProcessor,
		SipTransactionTerminatedProcessor, SipDialogTerminatedProcessor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.SipResponseProcessor#processResponse(javax.sip.ResponseEvent
	 * )
	 */
	@Override
	public void processResponse(ResponseEvent responseEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.SipRequestProcessor#processRequest(javax.sip.RequestEvent)
	 */
	@Override
	public void processRequest(RequestEvent requestEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.SipDialogTerminatedProcessor#processDialogTerminated(javax
	 * .sip.DialogTerminatedEvent)
	 */
	@Override
	public void processDialogTerminated(
			DialogTerminatedEvent dialogTerminatedEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.SipTransactionTerminatedProcessor#processTransactionTerminated
	 * (javax.sip.TransactionTerminatedEvent)
	 */
	@Override
	public void processTransactionTerminated(
			TransactionTerminatedEvent transactionTerminatedEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.SipIOExceptionProcessor#processIOException(javax.sip.
	 * IOExceptionEvent)
	 */
	@Override
	public void processIOException(IOExceptionEvent exceptionEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.SipTimeoutProcessor#processTimeout(javax.sip.TimeoutEvent)
	 */
	@Override
	public void processTimeout(TimeoutEvent timeoutEvent) {
		// TODO Auto-generated method stub

	}

}
