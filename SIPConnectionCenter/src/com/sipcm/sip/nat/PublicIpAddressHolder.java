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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sipcm.common.SystemConfiguration;

/**
 * @author wgao
 * 
 */
@Component("publicIpAddressHolder")
public class PublicIpAddressHolder {
	private static final Logger logger = LoggerFactory
			.getLogger(PublicIpAddressHolder.class);

	private static final int MAX_PORT_NUMBER = 65535;
	private static final int MIN_PORT_NUMBER = 1024;

	private static final Random portNumberGenerator = new Random();

	private InetAddress publicIp;

	@Resource(name = "systemConfiguration")
	private SystemConfiguration appConfig;

	@PostConstruct
	public void init() {
		if (appConfig.isUseStun()) {
			probePublicIp();
		}
	}

	private void probePublicIp() {
		if (logger.isDebugEnabled()) {
			logger.debug("Getting public IP address.");
		}
		try {
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
						appConfig.getStunServerAddress(),
						appConfig.getStunServerPort());

				NetworkConfigurationDiscoveryProcess addressDiscovery = new NetworkConfigurationDiscoveryProcess(
						localStunAddress, serverStunAddress);
				addressDiscovery.start();
				StunDiscoveryReport report = addressDiscovery
						.determineAddress();
				if (report.getPublicAddress() != null) {
					publicIp = report.getPublicAddress().getSocketAddress()
							.getAddress();
				} else {
					logger.error("Stun discovery failed to find a valid public ip address, disabling stun !");
				}
				if (publicIp != null) {
					if (logger.isInfoEnabled()) {
						logger.info("Using public IP address: \"{}\".",
								publicIp.getHostAddress());
					}
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Cannot detect public IP address.");
					}
				}
				addressDiscovery.shutDown();
			}
		} catch (Exception ex) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"A problem occured while setting up public IP address ",
						ex);
			}
		}
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
		int currentlyTriedPort = getRandomPortNumber(MIN_PORT_NUMBER,
				MAX_PORT_NUMBER);

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
				currentlyTriedPort = getRandomPortNumber(MIN_PORT_NUMBER,
						MAX_PORT_NUMBER);
				logger.debug("Retrying bind on port {}", currentlyTriedPort);
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
