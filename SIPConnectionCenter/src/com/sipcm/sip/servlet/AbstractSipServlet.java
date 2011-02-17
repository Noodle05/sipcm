/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSessionsUtil;
import javax.servlet.sip.TimerService;
import javax.sip.message.Request;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
@Configurable
public abstract class AbstractSipServlet extends SipServlet implements Servlet {
	private static final long serialVersionUID = -2473822499443253930L;

	public static final String DOMAIN_NAME = "domainname";

	public static final String USER_ATTRIBUTE = "com.sipcm.user";
	public static final String USER_VOIP_ACCOUNT = "com.sipcm.voip.account";
	public static final String GV_WAITING_FOR_CALLBACK = "com.sipcm.googlevoice.waiting";
	public static final String APPLICATION_SESSION_ID = "com.sipcm.appsessionid.";
	public static final String CALLING_PHONE_NUMBER = "com.sipcm.calling.phonenumber";
	public static final String TARGET_USERSIPBINDING = "com.sipcm.target.userSipBinding";

	private static final String[] specialHandleRequest = new String[] {
			Request.ACK, Request.CANCEL, Request.BYE };
	static {
		Arrays.sort(specialHandleRequest);
	}

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected SipFactory sipFactory;

	protected SipSessionsUtil sipSessionsUtil;

	protected TimerService timeService;

	@Autowired
	@Qualifier("phoneNumberUtil")
	protected PhoneNumberUtil phoneNumberUtil;

	@Autowired
	@Qualifier("applicationConfiguration")
	protected Configuration appConfig;

	@Override
	public void init() throws ServletException {
		super.init();
		if (logger.isInfoEnabled()) {
			logger.info(getServletName() + " has been started");
		}
		sipFactory = (SipFactory) getServletContext().getAttribute(SIP_FACTORY);
		sipSessionsUtil = (SipSessionsUtil) getServletContext().getAttribute(
				SIP_SESSIONS_UTIL);
		timeService = (TimerService) getServletContext().getAttribute(
				TIMER_SERVICE);
		if (logger.isInfoEnabled()) {
			logger.info("Sip Factory ref from JNDI : " + sipFactory
					+ ", Sip Sessions Util ref from JNDI : " + sipSessionsUtil);
		}
	}

	protected String getDomain() {
		return appConfig.getString(DOMAIN_NAME);
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
