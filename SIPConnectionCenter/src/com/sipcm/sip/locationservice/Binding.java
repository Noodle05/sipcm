/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.io.Serializable;

import javax.servlet.sip.Address;

/**
 * @author wgao
 * 
 */
public class Binding implements Serializable, Comparable<Binding> {
	private static final long serialVersionUID = 6769447997175661766L;

	private Address address;

	private String callId;

	private long lastCheck;

	private Address remoteEnd;

	public Binding(Address address, Address remoteEnd, String callId) {
		this.address = address;
		this.remoteEnd = remoteEnd;
		this.callId = callId;
		lastCheck = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Binding o) {
		float q1 = address.getQ();
		float q2 = o.address.getQ();
		if (q1 > q2) {
			return 1;
		} else if (q2 > q1) {
			return -1;
		} else {
			int expiresTime1 = address.getExpires();
			int expiresTime2 = o.address.getExpires();
			if (expiresTime1 < expiresTime2) {
				return 1;
			} else if (expiresTime1 > expiresTime2) {
				return -1;
			}
			return 0;
		}
	}

	public Address getAddress() {
		return address;
	}

	public Address getRemoteEnd() {
		return remoteEnd;
	}

	public String getCallId() {
		return callId;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Binding[").append("Address=").append(address)
				.append("RemoteEnd=").append(remoteEnd).append(",CallId=")
				.append(callId).append("]");
		return sb.toString();
	}

	public void onContactExpire() {
		long now = System.currentTimeMillis();
		try {
			int time = (int) ((now - lastCheck) / 1000L);
			address.setExpires(address.getExpires() - time);
		} finally {
			lastCheck = now;
		}
	}
}
