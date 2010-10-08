/**
 * 
 */
package com.sipcm.sip;

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
@Component("sipServerListener")
public class SipServerListener implements SipListener {
	@Resource(name = "sipServerRequestProcessor")
	private SipServerRequestProcessCenter requestProcessor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processRequest(javax.sip.RequestEvent)
	 */
	@Override
	public void processRequest(RequestEvent requestEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processResponse(javax.sip.ResponseEvent)
	 */
	@Override
	public void processResponse(ResponseEvent responseEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processTimeout(javax.sip.TimeoutEvent)
	 */
	@Override
	public void processTimeout(TimeoutEvent timeoutEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sip.SipListener#processIOException(javax.sip.IOExceptionEvent)
	 */
	@Override
	public void processIOException(IOExceptionEvent exceptionEvent) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
