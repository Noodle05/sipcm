/**
 * 
 */
package com.sipcm.sip.proxy;

import java.util.Map;

import javax.annotation.Resource;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sipcm.sip.proxy.plugins.Method;

/**
 * @author wgao
 * 
 */
@Component("sipServerMessageProcessor")
public class SipServerMessageProcesser implements SipRequestProcessor,
		SipResponseProcessor, SipTimeoutProcessor, SipIOExceptionProcessor,
		SipTransactionTerminatedProcessor, SipDialogTerminatedProcessor {
	private static final Logger logger = LoggerFactory
			.getLogger(SipServerMessageProcesser.class);

	@Resource(name = "sipMessageFactory")
	private MessageFactory messageFactory;

	@Resource(name = "serverMethodMap")
	private Map<String, Method> methods;

	@Resource(name = "unknownMethod")
	private Method unknownMethod;

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
		Request request = requestEvent.getRequest();
		Method method = methods.get(request.getMethod());
		Response response = null;
		if (method == null) {
			method = unknownMethod;
		}
		try {
			response = method.processRequest(request);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error happened when process request: " + request,
						e);
			}
			try {
				response = messageFactory.createResponse(
						Response.SERVER_INTERNAL_ERROR, request);
			} catch (Exception ee) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot generate internal error response.", ee);
				}
			}
		}
		if (response != null) {
			try {
				ServerTransaction serverTransaction = requestEvent
						.getServerTransaction();
				if (serverTransaction != null) {
					serverTransaction.sendResponse(response);
				} else {
					final SipProvider sipProvider = (SipProvider) requestEvent
							.getSource();
					sipProvider.sendResponse(response);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error happened when sending response back.",
							e);
				}
			}
		}
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
