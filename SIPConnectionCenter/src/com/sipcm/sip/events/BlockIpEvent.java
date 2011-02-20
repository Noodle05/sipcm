/**
 * 
 */
package com.sipcm.sip.events;

import java.net.InetAddress;
import java.util.EventObject;

/**
 * @author wgao
 * 
 */
public class BlockIpEvent extends EventObject {
	private static final long serialVersionUID = 8157830456531783118L;

	public BlockIpEvent(InetAddress ips) {
		super(ips);
	}

	public InetAddress getIp() {
		return (InetAddress) source;
	}
}
