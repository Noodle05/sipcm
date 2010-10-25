/**
 * 
 */
package com.sipcm.sip.servlet;

import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;

/**
 * @author wgao
 * 
 */
public class ErrorListener extends AbstractSipServlet implements
		SipErrorListener {
	private static final long serialVersionUID = 7296367855301306642L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.sip.SipErrorListener#noAckReceived(javax.servlet.sip.
	 * SipErrorEvent)
	 */
	@Override
	public void noAckReceived(SipErrorEvent ee) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipErrorListener#noPrackReceived(javax.servlet.sip.
	 * SipErrorEvent)
	 */
	@Override
	public void noPrackReceived(SipErrorEvent ee) {
		// TODO Auto-generated method stub

	}

}
