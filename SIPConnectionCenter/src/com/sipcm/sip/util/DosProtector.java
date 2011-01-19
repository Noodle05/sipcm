/**
 * 
 */
package com.sipcm.sip.util;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.sip.SipServletRequest;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.MapEvictionListener;
import com.google.common.collect.MapMaker;
import com.sipcm.sip.events.BlockIpEventListener;
import com.sipcm.sip.events.BlockIpEventObject;

/**
 * @author wgao
 * 
 */
@Component("sip.DosProtector")
public class DosProtector implements MapEvictionListener<String, Boolean> {
	private static final Logger logger = LoggerFactory
			.getLogger(DosProtector.class);

	public static final String SIP_DOS_PROTECT_INTERVAL = "sip.dos.protect.interval";
	public static final String SIP_DOS_PROTECT_MAX_REQUESTS = "sip.dos.protect.max.requests";
	public static final String SIP_DOS_PROTECT_BLOCK_TIME = "sip.dos.protect.block.time";

	@Resource(name = "sip.dosBlockEventListener")
	private BlockIpEventListener blockEventListener;

	private ConcurrentMap<String, AtomicInteger> counter;
	private ConcurrentMap<String, Boolean> blockList;

	@Resource(name = "applicationConfiguration")
	private Configuration appConfig;

	@PostConstruct
	public void init() {
		counter = new MapMaker().concurrencyLevel(64)
				.expiration(getDosProtectInterval(), TimeUnit.SECONDS)
				.makeMap();
		blockList = new MapMaker().concurrencyLevel(64)
				.expiration(getDosProtectBlockTime(), TimeUnit.SECONDS)
				.evictionListener(this).makeMap();
		if (logger.isInfoEnabled()) {
			logger.info(
					"DosProtector enabled. Block remote IP that failed authentication more than \"{}\" times in \"{}\" seconds for \"{}\" seconds.",
					new Object[] { getDosProtectMaximumRequests(),
							getDosProtectInterval(), getDosProtectBlockTime() });
		}
	}

	public boolean checkDos(SipServletRequest request) {
		String ip = request.getInitialRemoteAddr();
		if (blockList.containsKey(ip)) {
			return false;
		}
		return true;
	}

	public void countAuthFailure(SipServletRequest request) {
		String ip = request.getInitialRemoteAddr();
		if (blockList.containsKey(ip)) {
			return;
		}
		AtomicInteger count = counter.get(ip);
		if (count == null) {
			count = new AtomicInteger(0);
			AtomicInteger a = counter.putIfAbsent(ip, count);
			if (a != null) {
				count = a;
			}
		}
		int c = count.incrementAndGet();
		if (c > getDosProtectMaximumRequests()) {
			if (logger.isInfoEnabled()) {
				logger.info(
						"Remote address: \"{}\" authentication failed \"{}\" times in \"{}\" seconds, block it for \"{}\" seconds.",
						new Object[] { ip, c, getDosProtectInterval(),
								getDosProtectBlockTime() });
			}
			if (blockList.put(ip, true) == null) {
				if (blockEventListener != null) {
					try {
						InetAddress i = InetAddress.getByName(ip);
						blockEventListener.blockIp(new BlockIpEventObject(i));
					} catch (Exception e) {
						if (logger.isWarnEnabled()) {
							logger.warn(
									"Error happened when notify listener on block ip: "
											+ ip, e);
						}
					}
				}
			}
			counter.remove(ip);
		}
	}

	public void resetCounter(SipServletRequest request) {
		String ip = request.getInitialRemoteAddr();
		if (blockList.remove(ip) != null) {
			if (blockEventListener != null) {
				try {
					InetAddress i = InetAddress.getByName(ip);
					blockEventListener.unblockIp(new BlockIpEventObject(i));
				} catch (Exception e) {
					if (logger.isWarnEnabled()) {
						logger.warn(
								"Error happened when notify listener on block ip: "
										+ ip, e);
					}
				}
			}
		}
		counter.remove(ip);
	}

	public long getDosProtectInterval() {
		return appConfig.getLong(SIP_DOS_PROTECT_INTERVAL, 60L);
	}

	public int getDosProtectMaximumRequests() {
		return appConfig.getInt(SIP_DOS_PROTECT_MAX_REQUESTS, 10);
	}

	public long getDosProtectBlockTime() {
		return appConfig.getLong(SIP_DOS_PROTECT_BLOCK_TIME, 3600L);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.common.collect.MapEvictionListener#onEviction(java.lang.Object
	 * , java.lang.Object)
	 */
	@Override
	public void onEviction(String key, Boolean value) {
		if (logger.isInfoEnabled()) {
			logger.info("Remote address blocking expired.");
		}
		if (blockEventListener != null) {
			try {
				InetAddress i = InetAddress.getByName(key);
				blockEventListener.unblockIp(new BlockIpEventObject(i));
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Error happened when notify listener on block ip: "
									+ key, e);
				}
			}
		}
	}
}
