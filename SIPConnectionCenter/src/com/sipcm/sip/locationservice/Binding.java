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

	public Binding(Address address, String callId) {
		this.address = address;
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
			return 0;
		}
	}

	public Address getAddress() {
		return address;
	}

	public String getCallId() {
		return callId;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Binding[").append("Address=").append(address)
				.append(",CallId=").append(callId).append("]");
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