/**
 * 
 */
package com.sipcm.sip.proxy;

import javax.annotation.Resource;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

import org.springframework.stereotype.Component;


/**
 * @author wgao
 * 
 */
@Component("sipClientListener")
public class SipClientListener implements SipListener {
	@Resource(name = "sipClientMessageProcessor")
	private SipRequestProcessor requestProcessor;

	@Resource(name = "sipClientMessageProcessor")
	private SipResponseProcessor responseProcessor;

	@Resource(name = "sipClientMessageProcessor")
	private SipTimeoutProcessor timeoutProcessor;

	@Resource(name = "sipClientMessageProcessor")
	private SipIOExceptionProcessor ioExceptionProcessor;

	@Resource(name = "sipClientMessageProcessor")
	private SipTransactionTerminatedProcessor transactionTerminatedProcessor;

	@Resource(name = "sipClientMessageProcessor")
	private SipDialogTerminatedProcessor dialogTerminatedProcessor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processRequest(javax.sip.RequestEvent)
	 */
	@Override
	public void processRequest(RequestEvent requestEvent) {
		requestProcessor.processRequest(requestEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processResponse(javax.sip.ResponseEvent)
	 */
	@Override
	public void processResponse(ResponseEvent responseEvent) {
		responseProcessor.processResponse(responseEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processTimeout(javax.sip.TimeoutEvent)
	 */
	@Override
	public void processTimeout(TimeoutEvent timeoutEvent) {
		timeoutProcessor.processTimeout(timeoutEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processIOException(javax.sip.IOExceptionEvent)
	 */
	@Override
	public void processIOException(IOExceptionEvent exceptionEvent) {
		ioExceptionProcessor.processIOException(exceptionEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processTransactionTerminated(javax.sip.
	 * TransactionTerminatedEvent)
	 */
	@Override
	public void processTransactionTerminated(
			TransactionTerminatedEvent transactionTerminatedEvent) {
		transactionTerminatedProcessor
				.processTransactionTerminated(transactionTerminatedEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.sip.SipListener#processDialogTerminated(javax.sip.DialogTerminatedEvent
	 * )
	 */
	@Override
	public void processDialogTerminated(
			DialogTerminatedEvent dialogTerminatedEvent) {
		dialogTerminatedProcessor
				.processDialogTerminated(dialogTerminatedEvent);
	}

}
