/**
 * 
 */
package com.sipcm.sip.servlet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.sip.annotation.SipListener;

import org.mobicents.servlet.sip.SipConnector;
import org.mobicents.servlet.sip.listener.SipConnectorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.sip.vendor.VoipVendorManager;

/**
 * @author wgao
 * 
 */
@Configurable
@SipListener
public class SipConnectorListenImpl implements SipConnectorListener {
	private static final Logger logger = LoggerFactory
			.getLogger(SipConnectorListenImpl.class);

	@Autowired
	@Qualifier("voipVendorManager")
	private VoipVendorManager voipVendorManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.servlet.sip.listener.SipConnectorListener#sipConnectorAdded
	 * (org.mobicents.servlet.sip.SipConnector)
	 */
	@Override
	public void sipConnectorAdded(SipConnector connector) {
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
						logger.debug("System is listening on \"{}\", port: {}",
								listeningIp.getHostAddress(), listeningPort);
					}
					voipVendorManager.setListeningAddress(listeningIp,
							listeningPort);
				}
			} catch (UnknownHostException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot determine listening address.", e);
				}
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

}
