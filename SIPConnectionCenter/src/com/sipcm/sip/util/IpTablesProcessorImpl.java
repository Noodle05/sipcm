/**
 * 
 */
package com.sipcm.sip.util;

import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSchException;
import com.sipcm.common.SystemConfiguration;

/**
 * @author wgao
 * 
 */
@Component("ipTablesBlockProcessor")
public class IpTablesProcessorImpl implements IpTablesProcessor {
	private static final Logger logger = LoggerFactory
			.getLogger(IpTablesProcessorImpl.class);

	private static final String IP_ELEMENT = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	private static final String IPADDR = IP_ELEMENT + "\\." + IP_ELEMENT
			+ "\\." + IP_ELEMENT + "\\." + IP_ELEMENT;
	private static final Pattern rule = Pattern.compile("(\\d+)\\:(" + IPADDR
			+ ")");

	private static final String LIST_ALL_BLOCK_COMMAND = "/usr/sbin/iptables -L FORWARD -n -v --line-numbers "
			+ "| /bin/egrep \".*DROP.*udp dpt:5060\" "
			+ "| /usr/bin/awk -F \" \" '{print $1 \":\" $9}'";
	private static final String LIST_BLOCK_IP_COMMAND = "/usr/sbin/iptables -L FORWARD -n -v --line-numbers "
			+ "| /bin/egrep \".*DROP.*{0}.*udp dpt:5060\" "
			+ "| /usr/bin/awk -F \" \" '''{print $1 \":\" $9}'''";
	private static final String REMOVE_BLOCK_COMMAND = "/usr/sbin/iptables -D FORWARD {0}";
	private static final String ADD_BLOCK_IP_COMMAND = "/usr/sbin/iptables -I FORWARD 1 -p udp -s {0} --dport 5060 -j DROP";

	@Resource(name = "systemConfiguration")
	private SystemConfiguration appConfig;

	@Resource(name = "sshExecutor")
	private SshExecutor sshExecutor;

	private enum RequestType {
		BLOCK, UNBLOCK, REMOVEALL;
	}

	private final BlockingQueue<Request> requests;
	private final Lock requestsLock;

	private final AtomicBoolean running;

	public IpTablesProcessorImpl() {
		requests = new LinkedBlockingQueue<Request>();
		running = new AtomicBoolean(false);
		requestsLock = new ReentrantLock();
	}

	@PostConstruct
	public void init() {
		if (appConfig.isFirewallEnabled()) {
			sshExecutor.init(appConfig.getFirewallHost(),
					appConfig.getFirewallUser(), appConfig.getKnownHostsFile(),
					appConfig.getPrivateKeyFile(),
					appConfig.getPasswordPhrase(),
					appConfig.getSshDisconnectDelay());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.util.IpTablesProcess#postBlockRequest(java.net.InetAddress)
	 */
	@Override
	public boolean postBlockRequest(InetAddress ip) {
		if (appConfig.isFirewallEnabled()) {
			if (ip != null) {
				requestsLock.lock();
				try {
					Iterator<Request> ite = requests.iterator();
					while (ite.hasNext()) {
						Request r = ite.next();
						if (ip.equals(r.ip)) {
							if (logger.isTraceEnabled()) {
								logger.trace(
										"Find existing request for ip \"{}\", remove it.",
										ip);
							}
							ite.remove();
						}
					}
					return requests.offer(new Request(RequestType.BLOCK, ip));
				} finally {
					requestsLock.unlock();
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.util.IpTablesProcess#postUnblockIp(java.net.InetAddress)
	 */
	@Override
	public boolean postUnblockIp(InetAddress ip) {
		if (appConfig.isFirewallEnabled()) {
			if (ip != null) {
				requestsLock.lock();
				try {
					Iterator<Request> ite = requests.iterator();
					while (ite.hasNext()) {
						Request r = ite.next();
						if (ip.equals(r.ip)) {
							if (logger.isTraceEnabled()) {
								logger.trace(
										"Find existing request for ip \"{}\", remove it.",
										ip);
							}
							ite.remove();
						}
					}
					return requests.offer(new Request(RequestType.UNBLOCK, ip));
				} finally {
					requestsLock.unlock();
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.util.IpTablesProcess#postRemoveAll()
	 */
	@Override
	public boolean postRemoveAll() {
		if (appConfig.isFirewallEnabled()) {
			requestsLock.lock();
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Remove all existing requests.");
				}
				requests.clear();
				return requests.offer(new Request(RequestType.REMOVEALL, null));
			} finally {
				requestsLock.unlock();
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.util.IpTablesProcess#process()
	 */
	@Override
	@Async
	public void process() {
		if (appConfig.isFirewallEnabled()) {
			if (running.compareAndSet(false, true)) {
				try {
					Request request;
					while ((request = getNextRequest()) != null) {
						try {
							processRequest(request);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error(
										"Error happened when process request: "
												+ request, e);
							}
						}
					}
				} finally {
					running.set(false);
				}
			}
		}
	}

	private Request getNextRequest() {
		requestsLock.lock();
		try {
			return requests.poll();
		} finally {
			requestsLock.unlock();
		}
	}

	private void processRequest(Request request) throws JSchException,
			IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("Processing request: \"{}\"", request);
		}
		switch (request.requestType) {
		case REMOVEALL:
			removeBlockIp(null);
			break;
		case BLOCK:
			blockIp(request.ip);
			break;
		case UNBLOCK:
			removeBlockIp(request.ip);
			break;
		}
	}

	private void removeBlockIp(InetAddress ip) throws JSchException,
			IOException {
		Map<Integer, InetAddress> rules = getRuleNumberByIp(ip);
		if (rules != null) {
			if (rules.size() > 1) {
				SortedMap<Integer, InetAddress> sortedRules = new TreeMap<Integer, InetAddress>(
						new Comparator<Integer>() {
							@Override
							public int compare(Integer o1, Integer o2) {
								return o2 - o1;
							}
						});
				sortedRules.putAll(rules);
				rules = sortedRules;
			}
			for (Entry<Integer, InetAddress> entry : rules.entrySet()) {
				Integer ruleNumber = entry.getKey();
				InetAddress i = entry.getValue();
				if (logger.isTraceEnabled()) {
					logger.trace("Find rule: {} which block ip: \"{}\"",
							ruleNumber, i.getHostAddress());
				}
				removeRule(ruleNumber);
			}
		}
	}

	private void removeRule(int ruleNumber) throws JSchException, IOException {
		String command = MessageFormat.format(REMOVE_BLOCK_COMMAND, ruleNumber);
		SshExecuteResult result = sshExecutor.executeCommand(command);
		if (result.getExitStatus() == 0) {
			if (logger.isInfoEnabled()) {
				logger.info("Rule {} had been removed.", ruleNumber);
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("SshExecutor return error code: {}, Error output:",
						result.getExitStatus());
				for (String s : result.getError()) {
					logger.warn("\t{}", s);
				}
			}
		}
	}

	private void blockIp(InetAddress ip) throws JSchException, IOException {
		Map<Integer, InetAddress> existingRule = getRuleNumberByIp(ip);
		if (existingRule == null || existingRule.isEmpty()) {
			String command = MessageFormat.format(ADD_BLOCK_IP_COMMAND,
					ip.getHostAddress());
			SshExecuteResult result = sshExecutor.executeCommand(command);
			if (result.getExitStatus() == 0) {
				if (logger.isInfoEnabled()) {
					logger.info("Rule had been added to block ip:\"{}\".",
							ip.getHostAddress());
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"SshExecutor return error code: {}, Error output:",
							result.getExitStatus());
					for (String s : result.getError()) {
						logger.warn("\t{}", s);
					}
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Block rule for ip \"{}\" already exists.", ip);
			}
		}
	}

	private Map<Integer, InetAddress> getRuleNumberByIp(InetAddress ip)
			throws JSchException, IOException {
		String command;
		if (ip == null) {
			command = LIST_ALL_BLOCK_COMMAND;
		} else {
			command = MessageFormat.format(LIST_BLOCK_IP_COMMAND,
					ip.getHostAddress());
		}
		SshExecuteResult result = sshExecutor.executeCommand(command);
		if (result.getExitStatus() == 0) {
			Collection<String> output = result.getOutput();
			return parseOutput(output);
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("SshExecutor return error code: {}, Error output:",
						result.getExitStatus());
				for (String s : result.getError()) {
					logger.warn("\t{}", s);
				}
			}
			return null;
		}
	}

	private Map<Integer, InetAddress> parseOutput(Collection<String> strs) {
		Map<Integer, InetAddress> result = new HashMap<Integer, InetAddress>(
				strs.size());
		for (String str : strs) {
			try {
				Matcher m = rule.matcher(str);
				if (m.matches()) {
					String numStr = m.group(1);
					String ipStr = m.group(2);
					int num = Integer.parseInt(numStr);
					InetAddress ip = InetAddress.getByName(ipStr);
					result.put(num, ip);
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("I do not recorganize this line \"{}\".",
								str);
					}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error happened? Noway. String: + \"" + str
							+ "\"", e);
				}
			}
		}
		return result;
	}

	private class Request {
		private RequestType requestType;
		private InetAddress ip;

		private Request(RequestType requestType, InetAddress ip) {
			this.requestType = requestType;
			this.ip = ip;
		}

		public String toString() {
			return "Request[type=" + requestType + ",ip=" + ip.getHostAddress()
					+ "]";
		}
	}
}
