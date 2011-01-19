/**
 * 
 */
package com.sipcm.sip.util;

import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sipcm.sip.events.BlockIpEventListener;
import com.sipcm.sip.events.BlockIpEventObject;

/**
 * @author wgao
 * 
 */
@Component("sip.dosBlockEventListener")
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
	 * @see com.sipcm.sip.nat.BlockEventListener#blockIps(com.sipcm.sip.nat.
	 * BlockIpEventObject)
	 */
	@Override
	public void blockIp(BlockIpEventObject event) {
		InetAddress ip = event.getSource();
		if (processor.postBlockRequest(ip)) {
			processor.process();
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
		if (processor.postUnblockIp(ip)) {
			processor.process();
		}
	}
}
