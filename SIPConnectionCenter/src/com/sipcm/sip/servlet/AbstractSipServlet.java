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

	public static final String US_CA_NUMBER = "\\d{7}|(?:\\+1|1)?\\d{10}";
	public static final String INTERNATIONAL_NUMBER = "(?:\\+|011)[^1]\\d{7,}";

	public static final Pattern PHONE_NUMBER = Pattern.compile("^(\\+?\\d+)$");
	public static final Pattern US_CA_NUMBER_PATTERN = Pattern
			.compile("^(\\d{7}|(?:\\+1|1)?\\d{10})$");
	public static final Pattern INTERNATIONAL_NUMBER_PATTERN = Pattern
			.compile("^((?:\\+|011)[^1]\\d{7,})$");

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected SipFactory sipFactory;

	protected SipSessionsUtil sipSessionsUtil;

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
}
