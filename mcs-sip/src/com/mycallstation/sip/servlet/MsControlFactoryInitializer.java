/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;

import javax.media.mscontrol.MsControlFactory;
import javax.media.mscontrol.spi.Driver;
import javax.media.mscontrol.spi.DriverManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.mycallstation.sip.util.SipConfiguration;

/**
 * @author wgao
 * 
 */
public class MsControlFactoryInitializer implements ServletContextListener {
	private static final Logger logger = LoggerFactory
			.getLogger(MsControlFactoryInitializer.class);

	public static final String MS_CONTROL_FACTORY = "MsControlFactory";
	public static final String MOBICENTS_DRIVER_NAME = "org.mobicents.Driver_1.0";

	// Property key for the Unique MGCP stack name for this application
	public static final String MGCP_STACK_NAME = "mgcp.stack.name";
	// Property key for the IP address where CA MGCP Stack (SIP Servlet
	// Container) is bound
	public static final String MGCP_STACK_IP = "mgcp.server.address";
	// Property key for the port where CA MGCP Stack is bound
	public static final String MGCP_STACK_PORT = "mgcp.local.port";
	// Property key for the IP address where MGW MGCP Stack (MMS) is bound
	public static final String MGCP_PEER_IP = "mgcp.bind.address";
	// Property key for the port where MGW MGCP Stack is bound
	public static final String MGCP_PEER_PORT = "mgcp.server.port";

	private volatile SipConfiguration appConfig;
	private ServletContext ctx;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		Iterator<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasNext()) {
			Driver driver = drivers.next();
			DriverManager.deregisterDriver(driver);
			try {
				Method method = driver.getClass().getMethod("shutdown",
						new Class[] {});
				if (method != null) {
					method.invoke(driver, new Object[] {});
				}
			} catch (Exception e) {
				if (logger.isInfoEnabled()) {
					logger.info("Cannot execute shutdown on driver, may not exists. Turn on debug to check exception stack.");
					if (logger.isDebugEnabled()) {
						logger.debug("Detail exception stack:", e);
					}
				}
			}
		}
		ctx = null;
		appConfig = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ctx = event.getServletContext();
		if (event.getServletContext().getAttribute(MS_CONTROL_FACTORY) == null) {
			Properties properties = new Properties();
			properties.setProperty(MGCP_STACK_NAME, getMGCPStackName());
			properties.setProperty(MGCP_PEER_IP, getMGCPPeerIP());
			properties.setProperty(MGCP_PEER_PORT,
					Integer.toString(getMGCPPeerPort()));
			properties.setProperty(MGCP_STACK_IP, getLocalAddress());
			properties.setProperty(MGCP_STACK_PORT,
					Integer.toString(getLocalPort()));
			try {
				Driver driver = DriverManager.getDriver(MOBICENTS_DRIVER_NAME);
				final MsControlFactory msControlFactory = driver
						.getFactory(properties);
				event.getServletContext().setAttribute(MS_CONTROL_FACTORY,
						msControlFactory);
				if (logger.isInfoEnabled()) {
					logger.info("started MGCP Stack on {} and port {}",
							getLocalAddress(), getLocalPort());
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("count's start the underlying MGCP Stack", e);
				}
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("MGCP Stack already started on {} and port {}",
						getLocalAddress(), getLocalPort());
			}
		}
	}

	private int getMGCPPeerPort() {
		return getAppConfig().getMGCPPeerPort();
	}

	private String getMGCPPeerIP() {
		return getAppConfig().getMGCPPeerIP();
	}

	private String getMGCPStackName() {
		return getAppConfig().getMGCPStackName();
	}

	private String getLocalAddress() {
		return getAppConfig().getMGCPLocalAddress();
	}

	private int getLocalPort() {
		return getAppConfig().getMGCPLocalPort();
	}

	private SipConfiguration getAppConfig() {
		if (appConfig == null) {
			synchronized (this) {
				if (appConfig == null) {
					WebApplicationContext appCtx = WebApplicationContextUtils
							.getWebApplicationContext(ctx);
					appConfig = appCtx.getBean("systemConfiguration",
							SipConfiguration.class);
				}
			}
		}
		return appConfig;
	}
}
