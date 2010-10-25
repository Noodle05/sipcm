/**
 * 
 */
package com.sipcm.sip.servlet;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;

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

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected SipFactory sipFactory;

	@Autowired
	@Qualifier("applicationConfiguration")
	protected Configuration appConfig;

	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("the simple sip servlet has been started");
		try {
			// Getting the Sip factory from the JNDI Context
			Properties jndiProps = new Properties();
			Context initCtx = new InitialContext(jndiProps);
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			sipFactory = (SipFactory) envCtx
					.lookup("sip/com.sipcm.CallCenter/SipFactory");
			logger.info("Sip Factory ref from JNDI : " + sipFactory);
		} catch (NamingException e) {
			throw new ServletException("Uh oh -- JNDI problem !", e);
		}
	}

	protected String getDomain() {
		return appConfig.getString(DOMAIN_NAME);
	}
}
