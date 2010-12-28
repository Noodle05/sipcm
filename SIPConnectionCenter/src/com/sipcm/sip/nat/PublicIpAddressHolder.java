/**
 * 
 */
package com.sipcm.sip.nat;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.java.stun4j.StunAddress;
import net.java.stun4j.client.NetworkConfigurationDiscoveryProcess;
import net.java.stun4j.client.StunDiscoveryReport;

import org.apache.commons.configuration.Configuration;
import org.mobicents.servlet.sip.JainSipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("publicIpAddressHolder")
public class PublicIpAddressHolder {
	private static final Logger logger = LoggerFactory
			.getLogger(PublicIpAddressHolder.class);

	public static final String USE_STUN = "sip.useStun";
	public static final String STUN_SERVER = "sip.stun.server";
	public static final String STUN_PORT = "sip.stun.port";

	private static Random portNumberGenerator = new Random();

	private InetAddress publicIp;

	@Resource(name = "applicationConfiguration")
	private Configuration appConfig;

	@PostConstruct
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting public IP address.");
		}
		try {
			boolean useStun = isUseStun();
			if (useStun) {
				InetAddress ipAddress = InetAddress.getLocalHost();
				if (ipAddress.isLoopbackAddress()) {
					logger.warn("The Ip address provided is the loopback address, stun won't be enabled for it");
				} else {
					// chooses stun port randomly
					DatagramSocket randomSocket = initRandomPortSocket();
					int randomPort = randomSocket.getLocalPort();
					randomSocket.disconnect();
					randomSocket.close();
					randomSocket = null;
					StunAddress localStunAddress = new StunAddress(ipAddress,
							randomPort);

					StunAddress serverStunAddress = new StunAddress(
							getStunServerAddress(), getStunServerPort());

					NetworkConfigurationDiscoveryProcess addressDiscovery = new NetworkConfigurationDiscoveryProcess(
							localStunAddress, serverStunAddress);
					addressDiscovery.start();
					StunDiscoveryReport report = addressDiscovery
							.determineAddress();
					if (report.getPublicAddress() != null) {
						publicIp = report.getPublicAddress().getSocketAddress()
								.getAddress();
					} else {
						useStun = false;
						logger.error("Stun discovery failed to find a valid public ip address, disabling stun !");
					}
					if (logger.isInfoEnabled()) {
						logger.info("Stun report = " + report);
					}
					addressDiscovery.shutDown();
				}
			}
		} catch (Exception ex) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"A problem occured while setting up public IP address ",
						ex);
			}
		}
	}

	private boolean isUseStun() {
		return appConfig.getBoolean(USE_STUN, true);
	}

	private String getStunServerAddress() {
		return appConfig.getString(STUN_SERVER, "stun.counterpath.com");
	}

	private int getStunServerPort() {
		return appConfig.getInt(STUN_PORT, 3478);
	}

	/**
	 * Initializes and binds a socket that on a random port number. The method
	 * would try to bind on a random port and retry 5 times until a free port is
	 * found.
	 * 
	 * @return the socket that we have initialized on a randomport number.
	 */
	private DatagramSocket initRandomPortSocket() {
		int bindRetries = 5;
		int currentlyTriedPort = getRandomPortNumber(
				JainSipUtils.MIN_PORT_NUMBER, JainSipUtils.MAX_PORT_NUMBER);

		DatagramSocket resultSocket = null;
		// we'll first try to bind to a random port. if this fails we'll try
		// again (bindRetries times in all) until we find a free local port.
		for (int i = 0; i < bindRetries; i++) {
			try {
				resultSocket = new DatagramSocket(currentlyTriedPort);
				// we succeeded - break so that we don't try to bind again
				break;
			} catch (SocketException exc) {
				if (exc.getMessage().indexOf("Address already in use") == -1) {
					logger.error("An exception occurred while trying to create"
							+ "a local host discovery socket.", exc);
					return null;
				}
				// port seems to be taken. try another one.
				logger.debug("Port " + currentlyTriedPort + " seems in use.");
				currentlyTriedPort = getRandomPortNumber(
						JainSipUtils.MIN_PORT_NUMBER,
						JainSipUtils.MAX_PORT_NUMBER);
				logger.debug("Retrying bind on port " + currentlyTriedPort);
			}
		}

		return resultSocket;
	}

	private static int getRandomPortNumber(int min, int max) {
		return portNumberGenerator.nextInt(max - min) + min;
	}

	public InetAddress getPublicIp() {
		return publicIp;
	}
}
