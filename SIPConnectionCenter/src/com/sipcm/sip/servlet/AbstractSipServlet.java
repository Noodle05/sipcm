/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSessionsUtil;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

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
	public static final String ORIGINAL_REQUEST = "com.sipcm.originalRequest";
	public static final String GV_WAITING_FOR_CALLBACK = "com.sipcm.googlevoice.waiting";
	public static final String APPLICATION_SESSION_ID = "com.sipcm.appsessionid.";

	public static final Pattern PHONE_NUMBER = Pattern.compile("^(\\+?\\d+)$");

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected SipFactory sipFactory;

	protected SipSessionsUtil sipSessionsUtil;

	@Autowired
	@Qualifier("applicationConfiguration")
	protected Configuration appConfig;

	@Override
	public void init() throws ServletException {
		super.init();
		if (logger.isInfoEnabled()) {
			logger.info(getServletName() + " has been started");
		}
		try {
			// Getting the Sip factory from the JNDI Context
			Properties jndiProps = new Properties();
			Context initCtx = new InitialContext(jndiProps);
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			sipFactory = (SipFactory) envCtx
					.lookup("sip/org.gaofamily.CallCenter/SipFactory");
			sipSessionsUtil = (SipSessionsUtil) envCtx
					.lookup("sip/org.gaofamily.CallCenter/SipSessionsUtil");
			if (logger.isInfoEnabled()) {
				logger.info("Sip Factory ref from JNDI : " + sipFactory
						+ ", Sip Sessions Util ref from JNDI : "
						+ sipSessionsUtil);
			}
		} catch (NamingException e) {
			throw new ServletException("Uh oh -- JNDI problem !", e);
		}
	}

	protected String getDomain() {
		return appConfig.getString(DOMAIN_NAME);
	}

	protected void responseError(SipServletRequest req, int statusCode)
			throws IOException {
		responseError(req, statusCode, null);
	}

	protected void responseError(SipServletRequest req, int statusCode,
			String reasonPhrase) throws IOException {
		SipServletResponse response = req.createResponse(statusCode,
				reasonPhrase);
		response.send();
	}

	/**
	 * Get digital only format phone number.
	 * 
	 * @param myNumber
	 * @return
	 */
	protected String getCanonicalizedPhoneNumber(String phoneNumber) {
		String newNumber = phoneNumber.replaceAll("[^\\+|^\\d]", "");
		if (newNumber.startsWith("+")) {
			newNumber = "011" + newNumber.substring(1);
		}
		return newNumber;
	}
}
