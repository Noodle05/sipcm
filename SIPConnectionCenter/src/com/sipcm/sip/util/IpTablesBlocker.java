/**
 * 
 */
package com.sipcm.sip.util;

import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.sip.events.BlockIpEvent;
import com.sipcm.sip.events.BlockIpEventListener;

/**
 * @author wgao
 * 
 */
@Component("sipDosBlockEventListener")
public class IpTablesBlocker implements BlockIpEventListener {
	@Resource(name = "ipTablesBlockProcessor")
	private IpTablesProcessor processor;

	@PostConstruct
	public void init() {
		if (processor.postRemoveAll()) {
			processor.process();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.BlockIpEventListener#blockIp(com.sipcm.sip.events
	 * .BlockIpEventObject)
	 */
	@Override
	public void blockIp(BlockIpEvent event) {
		InetAddress ip = event.getIp();
		if (processor.postBlockRequest(ip)) {
			processor.process();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.events.BlockIpEventListener#unblockIp(com.sipcm.sip.events
	 * .BlockIpEventObject)
	 */
	@Override
	public void unblockIp(BlockIpEvent event) {
		InetAddress ip = event.getIp();
		if (processor.postUnblockIp(ip)) {
			processor.process();
		}
	}
}
