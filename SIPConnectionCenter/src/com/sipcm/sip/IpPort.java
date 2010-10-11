/**
 * 
 */
package com.sipcm.sip;

import java.net.InetAddress;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author wgao
 * 
 */
public class IpPort {
	private InetAddress ip;
	private int port;

	public IpPort(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(37, 53);
		hcb.append(ip);
		hcb.append(port);
		return hcb.toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof IpPort)) {
			return false;
		}
		final IpPort obj = (IpPort) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(ip, obj.ip);
		eb.append(port, obj.port);
		return eb.isEquals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ip.getHostAddress()).append(":").append(port);
		return sb.toString();
	}
}
