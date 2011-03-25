/**
 * 
 */
package com.mycallstation.sip.util;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.sip.SipServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.MapMaker;
import com.mycallstation.sip.events.BlockIpEvent;
import com.mycallstation.sip.events.BlockIpEventListener;

/**
 * @author wgao
 * 
 */
@Component("sipDosProtector")
public class DosProtector {
	private static final Logger logger = LoggerFactory
			.getLogger(DosProtector.class);

	@Resource(name = "sipDosBlockEventListener")
	private BlockIpEventListener blockEventListener;

	private ConcurrentMap<String, AtomicInteger> counter;
	private ConcurrentMap<String, Long> blockList;

	@Resource(name = "systemConfiguration")
	private SipConfiguration appConfig;

	@PostConstruct
	public void init() {
		counter = new MapMaker()
				.concurrencyLevel(32)
				.expireAfterWrite(appConfig.getDosProtectInterval(),
						TimeUnit.SECONDS).makeMap();
		blockList = new MapMaker().concurrencyLevel(4).makeMap();
		if (logger.isInfoEnabled()) {
			logger.info(
					"DosProtector enabled. Block remote IP that failed authentication more than \"{}\" times in \"{}\" seconds for \"{}\" seconds.",
					new Object[] { appConfig.getDosProtectMaximumRequests(),
							appConfig.getDosProtectInterval(),
							appConfig.getDosProtectBlockTime() });
		}
	}

	public boolean isDosAttach(SipServletRequest request) {
		String ip = request.getInitialRemoteAddr();
		if (blockList.containsKey(ip)) {
			return true;
		}
		return false;
	}

	public void countAttack(SipServletRequest request) {
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
		if (c > appConfig.getDosProtectMaximumRequests()) {
			if (logger.isInfoEnabled()) {
				logger.info(
						"Remote address: \"{}\" authentication failed \"{}\" times in \"{}\" seconds, block it for \"{}\" seconds.",
						new Object[] { ip, c,
								appConfig.getDosProtectInterval(),
								appConfig.getDosProtectBlockTime() });
			}
			if (blockList.putIfAbsent(ip,
					(System.currentTimeMillis() + appConfig
							.getDosProtectBlockTime() * 1000L)) == null) {
				if (blockEventListener != null) {
					try {
						InetAddress i = InetAddress.getByName(ip);
						blockEventListener.blockIp(new BlockIpEvent(i));
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
					blockEventListener.unblockIp(new BlockIpEvent(i));
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

	@Scheduled(fixedRate = 60000L)
	public void checkBlockExpire() {
		if (logger.isDebugEnabled()) {
			logger.debug("Check block list expire.");
		}
		Iterator<Entry<String, Long>> ite = blockList.entrySet().iterator();
		long now = System.currentTimeMillis();
		while (ite.hasNext()) {
			Entry<String, Long> entry = ite.next();
			String ip = entry.getKey();
			long expire = entry.getValue();
			if (now >= expire) {
				if (logger.isTraceEnabled()) {
					logger.trace("Blocking ip \"{}\" expired, remove it.", ip);
				}
				ite.remove();
				if (blockEventListener != null) {
					try {
						InetAddress i = InetAddress.getByName(ip);
						blockEventListener.unblockIp(new BlockIpEvent(i));
					} catch (Exception e) {
						if (logger.isWarnEnabled()) {
							logger.warn(
									"Error happened when notify listener on block ip: "
											+ ip, e);
						}
					}
				}
			}
		}
	}
}
