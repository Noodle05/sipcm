/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Resource;
import javax.servlet.sip.annotation.SipListener;

import org.mobicents.servlet.sip.SipConnector;
import org.mobicents.servlet.sip.listener.SipConnectorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.sip.nat.PublicIpAddressHolder;
import com.mycallstation.sip.vendor.VoipVendorManager;

/**
 * @author wgao
 * 
 */
@SipListener
public class SipConnectorListenerImpl implements SipConnectorListener {
	private static final Logger logger = LoggerFactory
			.getLogger(SipConnectorListenerImpl.class);

	@Resource(name = "voipVendorManager")
	private VoipVendorManager voipVendorManager;

	@Resource(name = "publicIpAddressHolder")
	private PublicIpAddressHolder publicIpAddressHolder;

	private volatile boolean setted;

	public SipConnectorListenerImpl() {
		setted = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.servlet.sip.listener.SipConnectorListener#sipConnectorAdded
	 * (org.mobicents.servlet.sip.SipConnector)
	 */
	@Override
	public void sipConnectorAdded(SipConnector connector) {
		if (voipVendorManager != null) {
			if (!setted) {
				if ("UDP".equalsIgnoreCase(connector.getTransport())) {
					try {
						InetAddress listeningIp = null;
						int listeningPort = 5060;
						if (connector.isUseStaticAddress()) {
							listeningIp = InetAddress.getByName(connector
									.getStaticServerAddress());
							listeningPort = connector.getStaticServerPort();
						} else {
							listeningIp = InetAddress.getByName(connector
									.getIpAddress());
							listeningPort = connector.getPort();
						}
						if (listeningIp != null) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										"System is listening on \"{}\", port: {}",
										listeningIp.getHostAddress(),
										listeningPort);
							}
							voipVendorManager.setListeningAddress(listeningIp,
									listeningPort);
							publicIpAddressHolder.setPublicIp(listeningIp);
							setted = true;
						}
					} catch (UnknownHostException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Cannot determine listening address.",
									e);
						}
					}
				}
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("No voip vendor manager object.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.servlet.sip.listener.SipConnectorListener#sipConnectorRemoved
	 * (org.mobicents.servlet.sip.SipConnector)
	 */
	@Override
	public void sipConnectorRemoved(SipConnector connector) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.context.ServletContextAware#setServletContext
	 * (javax.servlet.ServletContext)
	 */
	// @Override
	// public void setServletContext(ServletContext servletContext) {
	// voipVendorManager = (VoipVendorManager) servletContext
	// .getAttribute("voipVendorManager");
	// }
}
