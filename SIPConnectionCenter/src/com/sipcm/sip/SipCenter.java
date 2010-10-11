/**
 * 
 */
package com.sipcm.sip;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sipCenter")
public class SipCenter {
	public static final String SERVER_STACK_NAME = "sip.server.stackname";
	public static final String LISTEN_SERVER_INTERFACE = "sip.server.interfaces";
	public static final String LISTEN_SERVER_PORT = "sip.server.port";
	public static final String LISTEN_SERVER_TRANSPORT = "sip.server.transport";
	public static final String SIP_SERVER_LOGEVEL = "sip.server.loglevel";
	public static final String CLIENT_STACK_NAME = "sip.client.stackname";
	public static final String LISTEN_CLIENT_INTERFACE = "sip.client.interfaces";
	public static final String LISTEN_CLIENT_PORT = "sip.client.port";
	public static final String SIP_CLIENT_LOGEVEL = "sip.client.loglevel";
	public static final String IPV4_ONLY = "sip.ipv4.only";

	private static String SINGLE_IP = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	private static Pattern IP_PATTERN = Pattern.compile(SINGLE_IP + "\\."
			+ SINGLE_IP + "\\." + SINGLE_IP + "\\." + SINGLE_IP);

	@Resource(name = "applicationConfiguration")
	private Configuration config;

	private SipFactory sipFactory;
	private AddressFactory addressFactory;
	private MessageFactory messageFactory;
	private HeaderFactory headerFactory;
	private SipStack serverSipStack;
	private SipStack clientSipStack;

	@Resource(name = "sipServerListener")
	private SipListener serverListener;

	@Resource(name = "sipClientListener")
	private SipListener clientListener;

	private List<ListeningPoint> clientListeningPoints;

	@PostConstruct
	public void init() throws SocketException, InvalidArgumentException,
			SipException, TooManyListenersException {
		clientListeningPoints = new LinkedList<ListeningPoint>();

		boolean ipv4Only = config.getBoolean(IPV4_ONLY, true);
		String[] serverInterfaces = config
				.getStringArray(LISTEN_SERVER_INTERFACE);
		String[] clientInterfaces = config
				.getStringArray(LISTEN_CLIENT_INTERFACE);
		boolean serverUseAllInterface;
		boolean clientUseAllInterface;
		boolean clientSameAsServerInterface;
		if (ArrayUtils.isEquals(serverInterfaces, clientInterfaces)) {
			clientSameAsServerInterface = true;
		} else {
			clientSameAsServerInterface = false;
		}
		if (ArrayUtils.isEmpty(serverInterfaces)) {
			serverUseAllInterface = true;
		} else {
			serverUseAllInterface = false;
		}
		if (!clientSameAsServerInterface
				&& ArrayUtils.isEmpty(clientInterfaces)) {
			clientUseAllInterface = true;
		} else {
			clientUseAllInterface = false;
		}
		String[] serverIps = null;
		String[] serverNics = null;
		if (!serverUseAllInterface) {
			Collection<String> tips = new ArrayList<String>();
			Collection<String> tnics = new ArrayList<String>();
			for (String i : serverInterfaces) {
				if ("*".equals(i)) {
					serverUseAllInterface = true;
					break;
				} else {
					if (IP_PATTERN.matcher(i).matches()) {
						tips.add(i);
					} else {
						tnics.add(i);
					}
				}
			}
			if (!serverUseAllInterface) {
				serverIps = new String[tips.size()];
				serverIps = tips.toArray(serverIps);
				Arrays.sort(serverIps);
				serverNics = new String[tnics.size()];
				serverNics = tnics.toArray(serverNics);
				Arrays.sort(serverNics);
			}
		}
		String[] clientIps = null;
		String[] clientNics = null;
		if (!clientSameAsServerInterface && !clientUseAllInterface) {
			Collection<String> tips = new ArrayList<String>();
			Collection<String> tnics = new ArrayList<String>();
			for (String i : clientInterfaces) {
				if ("*".equals(i)) {
					clientUseAllInterface = true;
					break;
				} else {
					if (IP_PATTERN.matcher(i).matches()) {
						tips.add(i);
					} else {
						tnics.add(i);
					}
				}
			}
			if (!clientUseAllInterface) {
				clientIps = new String[tips.size()];
				clientIps = tips.toArray(clientIps);
				Arrays.sort(clientIps);
				clientNics = new String[tnics.size()];
				clientNics = tnics.toArray(clientNics);
				Arrays.sort(clientNics);
			}
		}

		Collection<InetAddress> serverAddrs = new ArrayList<InetAddress>();
		Collection<InetAddress> clientAddrs = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> eni = NetworkInterface
				.getNetworkInterfaces();
		while (eni.hasMoreElements()) {
			NetworkInterface ni = eni.nextElement();
			Enumeration<InetAddress> eia = ni.getInetAddresses();
			while (eia.hasMoreElements()) {
				InetAddress ia = eia.nextElement();
				if (serverUseAllInterface
						|| Arrays.binarySearch(serverNics, ni.getName()) >= 0
						|| Arrays.binarySearch(serverIps, ia.getHostAddress()) >= 0) {
					if (!ipv4Only || (ia instanceof Inet4Address)) {
						serverAddrs.add(ia);
					}
				}
				if (!clientSameAsServerInterface
						&& (clientUseAllInterface
								|| Arrays
										.binarySearch(clientNics, ni.getName()) >= 0 || Arrays
								.binarySearch(clientIps, ia.getHostAddress()) >= 0)) {
					if (!ipv4Only || (ia instanceof Inet4Address)) {
						clientAddrs.add(ia);
					}
				}
			}
		}
		if (clientSameAsServerInterface) {
			clientAddrs = serverAddrs;
		}
		if (serverAddrs.isEmpty()) {
			throw new BeanInitializationException(
					"Listen interface for server configuration error.");
		}
		if (clientAddrs.isEmpty()) {
			throw new BeanInitializationException(
					"Listen interface for client configuration error.");
		}
		int serverPort = config.getInt(LISTEN_SERVER_PORT, 5060);
		int clientPort = config.getInt(LISTEN_CLIENT_PORT, 5070);
		String transport = config.getString(LISTEN_SERVER_TRANSPORT, "udp");

		String serverStackName = config.getString(SERVER_STACK_NAME,
				"server.sipcm.com");
		String serverLogLevel = config.getString(SIP_SERVER_LOGEVEL, "32");

		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		addressFactory = sipFactory.createAddressFactory();
		headerFactory = sipFactory.createHeaderFactory();
		messageFactory = sipFactory.createMessageFactory();
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", serverStackName);
		properties
				.setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "1048576");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
				"SIPServerDebug.txt");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
				"SIPServerLog.txt");
		properties
				.setProperty("gov.nist.javax.sip.TRACE_LEVEL", serverLogLevel);
		// Drop the client connection after we are done with the transaction.
		properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
				"true");
		properties.setProperty("gov.nist.javax.sip.REENTRANT_LISTENER", "true");
		properties.setProperty("gov.nist.javax.sip.THREAD_POOL_SIZE", "50");
		serverSipStack = sipFactory.createSipStack(properties);

		for (InetAddress ia : serverAddrs) {
			ListeningPoint lp = serverSipStack.createListeningPoint(
					ia.getHostAddress(), serverPort, transport);
			SipProvider sipProvider = serverSipStack.createSipProvider(lp);
			sipProvider.addSipListener(serverListener);
		}

		String clientStackName = config.getString(CLIENT_STACK_NAME,
				"client.sipcm.com");
		String clientLogLevel = config.getString(SIP_CLIENT_LOGEVEL, "32");
		properties.clear();
		properties.setProperty("javax.sip.STACK_NAME", clientStackName);
		properties
				.setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "1048576");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
				"SIPClientDebug.txt");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
				"SIPClientLog.txt");
		properties
				.setProperty("gov.nist.javax.sip.TRACE_LEVEL", clientLogLevel);
		// Drop the client connection after we are done with the transaction.
		properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
				"true");
		properties.setProperty("gov.nist.javax.sip.REENTRANT_LISTENER", "true");
		properties.setProperty("gov.nist.javax.sip.THREAD_POOL_SIZE", "50");
		clientSipStack = sipFactory.createSipStack(properties);

		for (InetAddress ia : clientAddrs) {
			ListeningPoint lp = clientSipStack.createListeningPoint(
					ia.getHostAddress(), clientPort, transport);
			clientListeningPoints.add(lp);
			SipProvider sipProvider = clientSipStack.createSipProvider(lp);
			sipProvider.addSipListener(clientListener);
		}
	}

	@PreDestroy
	public void destroy() {
	}

	public void start() throws SipException {
		clientSipStack.start();
		serverSipStack.start();
	}

	public void stop() {
		serverSipStack.stop();
		clientSipStack.stop();
	}

	public AddressFactory getAddressFactory() {
		return addressFactory;
	}

	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}
}
