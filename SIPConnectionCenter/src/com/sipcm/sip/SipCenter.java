/**
 * 
 */
package com.sipcm.sip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
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
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("sipCenter")
public class SipCenter {
	public static final String STACK_NAME = "sip.stackname";
	public static final String LISTEN_INTERFACE = "sip.interfaces";
	public static final String LISTEN_PORT = "sip.port";
	public static final String LISTEN_TRANSPORT = "sip.transport";
	public static final String SIP_DEBUGLOGEVEL = "sip.loglevel";

	private static String SINGLE_IP = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	private static Pattern IP_PATTERN = Pattern.compile(SINGLE_IP + "\\."
			+ SINGLE_IP + "\\." + SINGLE_IP + "\\." + SINGLE_IP);

	@Resource(name = "applicationConfiguration")
	private Configuration config;

	private SipFactory sipFactory;
	private AddressFactory addressFactory;
	private MessageFactory messageFactory;
	private HeaderFactory headerFactory;
	private SipStack sipStack;

	@Resource(name = "sipServerListener")
	private SipListener listener;

	@PostConstruct
	public void init() throws SocketException, InvalidArgumentException,
			SipException, TooManyListenersException {
		String[] is = config.getStringArray(LISTEN_INTERFACE);
		boolean all;
		if (is == null) {
			all = true;
		} else {
			all = false;
		}
		String[] ips = null;
		String[] nics = null;
		if (!all) {
			Collection<String> tips = new ArrayList<String>();
			Collection<String> tnics = new ArrayList<String>();
			for (String i : is) {
				if ("*".equals(i)) {
					all = true;
					break;
				} else {
					if (IP_PATTERN.matcher(i).matches()) {
						tips.add(i);
					} else {
						tnics.add(i);
					}
				}
			}
			if (!all) {
				ips = new String[tips.size()];
				ips = tips.toArray(ips);
				Arrays.sort(ips);
				nics = new String[tnics.size()];
				nics = tnics.toArray(nics);
				Arrays.sort(nics);
			}
		}

		Collection<InetAddress> addrs = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> eni = NetworkInterface
				.getNetworkInterfaces();
		while (eni.hasMoreElements()) {
			NetworkInterface ni = eni.nextElement();
			Enumeration<InetAddress> eia = ni.getInetAddresses();
			while (eia.hasMoreElements()) {
				InetAddress ia = eia.nextElement();
				if (all || Arrays.binarySearch(nics, ni.getName()) >= 0
						|| Arrays.binarySearch(ips, ia.getHostAddress()) >= 0) {
					addrs.add(ia);
				}
			}
		}
		if (addrs.isEmpty()) {
			throw new BeanInitializationException(
					"Listen interface configuration error.");
		}
		int port = config.getInt(LISTEN_PORT, 5060);
		String transport = config.getString(LISTEN_TRANSPORT, "udp");
		String stackName = config.getString(STACK_NAME, "sipcm.com");
		String logLevel = config.getString(SIP_DEBUGLOGEVEL, "8");

		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		addressFactory = sipFactory.createAddressFactory();
		headerFactory = sipFactory.createHeaderFactory();
		messageFactory = sipFactory.createMessageFactory();
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", stackName);
		properties
				.setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "1048576");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
				"SIPServerDebug.txt");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
				"SIPServerLog.txt");
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "16");
		// Drop the client connection after we are done with the transaction.
		properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
				"true");
		properties.setProperty("gov.nist.javax.sip.REENTRANT_LISTENER", "true");
		properties.setProperty("gov.nist.javax.sip.THREAD_POOL_SIZE", "50");
		sipStack = sipFactory.createSipStack(properties);

		for (InetAddress ia : addrs) {
			ListeningPoint lp = sipStack.createListeningPoint(
					ia.getHostAddress(), port, transport);
			SipProvider sipProvider = sipStack.createSipProvider(lp);
			sipProvider.addSipListener(listener);
		}
		sipStack.start();
	}

	@PreDestroy
	public void destroy() {
		sipStack.stop();
	}
}
