/**
 * 
 */
package com.mycallstation.sip.servlet;

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

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.sip.events.CallEventListener;
import com.mycallstation.sip.util.SipConfiguration;

/**
 * @author wgao
 * 
 */
public abstract class AbstractSipServlet extends SipServlet implements Servlet {
	private static final long serialVersionUID = -2473822499443253930L;

	public static final String USER_ATTRIBUTE = "com.mycallstation.user";
	public static final String USER_VOIP_ACCOUNT = "com.mycallstation.voip.account";
	public static final String GV_WAITING_FOR_CALLBACK = "com.mycallstation.googlevoice.waiting";
	public static final String APPLICATION_SESSION_ID = "com.mycallstation.appsessionid.";
	public static final String CALLING_PHONE_NUMBER = "com.mycallstation.calling.phonenumber";
	public static final String TARGET_USERSIPBINDING = "com.mycallstation.target.userSipBinding";
	public static final String INCOMING_CALL_START = "com.mycallstation.incoming.start.event";
	public static final String OUTGOING_CALL_START = "com.mycallstation.outgoing.start.event";

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
	protected SipConfiguration appConfig;

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
