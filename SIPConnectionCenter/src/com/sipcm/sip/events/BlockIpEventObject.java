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
public class BlockIpEventObject extends EventObject {
	private static final long serialVersionUID = 8157830456531783118L;

	public BlockIpEventObject(InetAddress ips) {
		super(ips);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#getSource()
	 */
	@Override
	public InetAddress getSource() {
		return (InetAddress) source;
	}
}
