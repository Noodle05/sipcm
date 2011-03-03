/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSessionsUtil;
import javax.servlet.sip.TimerService;
import javax.sip.message.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipcm.common.SystemConfiguration;
import com.sipcm.sip.events.CallEventListener;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
public abstract class AbstractSipServlet extends SipServlet implements Servlet {
	private static final long serialVersionUID = -2473822499443253930L;

	public static final String USER_ATTRIBUTE = "org.gaofamily.user";
	public static final String USER_VOIP_ACCOUNT = "org.gaofamily.voip.account";
	public static final String GV_WAITING_FOR_CALLBACK = "org.gaofamily.googlevoice.waiting";
	public static final String APPLICATION_SESSION_ID = "org.gaofamily.appsessionid.";
	public static final String CALLING_PHONE_NUMBER = "org.gaofamily.calling.phonenumber";
	public static final String TARGET_USERSIPBINDING = "org.gaofamily.target.userSipBinding";
	public static final String INCOMING_CALL_START = "org.gaofamily.incoming.start.event";
	public static final String OUTGOING_CALL_START = "org.gaofamily.outgoing.start.event";

	private static final String[] specialHandleRequest = new String[] {
			Request.ACK, Request.CANCEL, Request.BYE };
	static {
		Arrays.sort(specialHandleRequest);
	}

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "sipCallEventListener")
	protected CallEventListener callEventListener;

	@Resource(name = "javax.servlet.sip.SipFactory")
	protected SipFactory sipFactory;

	@Resource(name = "javax.servlet.sip.SipSessionsUtil")
	protected SipSessionsUtil sipSessionsUtil;

	@Resource(name = "javax.servlet.sip.TimerService")
	protected TimerService timeService;

	@Resource(name = "systemConfiguration")
	protected SystemConfiguration appConfig;

	@Override
	public void init() throws ServletException {
		super.init();
		if (logger.isInfoEnabled()) {
			logger.info(getServletName() + " has been started");
		}
	}

	protected void response(SipServletRequest req, int statusCode)
			throws IOException {
		response(req, statusCode, null);
	}

	protected void response(SipServletRequest req, int statusCode,
			String reasonPhrase) throws IOException {
		SipServletResponse response = req.createResponse(statusCode,
				reasonPhrase);
		if (logger.isDebugEnabled()) {
			logger.debug("Sending response: {}", response);
		}
		response.send();
	}

	protected String generateAppSessionKey(UserSipProfile userSipProfile) {
		if (userSipProfile == null) {
			throw new NullPointerException("UserSipProfile is null.");
		}
		return APPLICATION_SESSION_ID + userSipProfile.getId();
	}

	protected boolean specialHandleRequest(SipServletRequest req) {
		return Arrays.binarySearch(specialHandleRequest, req.getMethod()) >= 0;
	}
}
