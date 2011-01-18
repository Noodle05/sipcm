/**
 * 
 */
package com.sipcm.sip.util;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelShell;
import com.sipcm.sip.events.BlockIpEventListener;
import com.sipcm.sip.events.BlockIpEventObject;

/**
 * @author wgao
 * 
 */
@Component("sip.dosBlockEventListener")
public class IpTablesBlocker implements BlockIpEventListener {
	private static final Logger logger = LoggerFactory
			.getLogger(IpTablesBlocker.class);

	private ChannelShell shell;

	private enum RequestType {
		BLOCK, UNBLOCK;
	}

	private final BlockingQueue<Request> requests;
	private final Lock requestsLock;

	private final AtomicBoolean running;

	public IpTablesBlocker() {
		requests = new LinkedBlockingQueue<Request>();
		running = new AtomicBoolean(false);
		requestsLock = new ReentrantLock();
	}

	@PostConstruct
	public void init() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.nat.BlockEventListener#blockIps(com.sipcm.sip.nat.
	 * BlockIpEventObject)
	 */
	@Override
	public void blockIp(BlockIpEventObject event) {
		InetAddress ip = event.getSource();
		if (ip != null) {
			requestsLock.lock();
			try {
				Iterator<Request> ite = requests.iterator();
				while (ite.hasNext()) {
					Request r = ite.next();
					if (ip.equals(r.ip)) {
						ite.remove();
					}
				}
				requests.offer(new Request(RequestType.BLOCK, ip));
				process();
			} finally {
				requestsLock.unlock();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.nat.BlockEventListener#unblockIps(com.sipcm.sip.nat.
	 * BlockIpEventObject)
	 */
	@Override
	public void unblockIp(BlockIpEventObject event) {
		InetAddress ip = event.getSource();
		if (ip != null) {
			requestsLock.lock();
			try {
				Iterator<Request> ite = requests.iterator();
				while (ite.hasNext()) {
					Request r = ite.next();
					if (ip.equals(r.ip)) {
						ite.remove();
					}
				}
				requests.offer(new Request(RequestType.UNBLOCK, ip));
				process();
			} finally {
				requestsLock.unlock();
			}
		}
	}

	@Async
	public void process() {
		if (running.compareAndSet(false, true)) {
			try {
				Request request;
				while ((request = getNextRequest()) != null) {
					processRequest(request);
				}
			} finally {
				running.set(false);
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

	private void processRequest(Request request) {
		// TODO:
	}

	private class Request {
		private RequestType requestType;
		private InetAddress ip;

		private Request(RequestType requestType, InetAddress ip) {
			this.requestType = requestType;
			this.ip = ip;
		}
		//
		// public int hashCode() {
		// final int prime = 11;
		// int result = 15;
		// result = prime * result
		// + ((requestType == null) ? 0 : requestType.hashCode());
		// result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		// return result;
		// }
		//
		// public boolean equals(Object other) {
		// if (this == other) {
		// return true;
		// }
		// if (other == null || !(other instanceof Request)) {
		// return false;
		// }
		// final Request obj = (Request) other;
		// if (requestType == null) {
		// if (obj.requestType != null) {
		// return false;
		// }
		// } else if (!requestType.equals(obj.requestType)) {
		// return false;
		// }
		// if (ip == null) {
		// if (obj.ip != null) {
		// return false;
		// }
		// } else if (!ip.equals(obj.ip)) {
		// return false;
		// }
		// return true;
		// }
	}
}
