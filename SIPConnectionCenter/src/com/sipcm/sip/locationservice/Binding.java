/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.io.Serializable;

import javax.sip.header.ContactHeader;

/**
 * @author wgao
 * 
 */
public class Binding implements Serializable, Comparable<Binding> {
	private static final long serialVersionUID = 6769447997175661766L;

	private ContactHeader contactHeader;

	private String callId;

	private long cseq;

	public Binding(ContactHeader contactHeader, String callId, long cseq) {
		this.contactHeader = contactHeader;
		this.callId = callId;
		this.cseq = cseq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Binding o) {
		float q1 = contactHeader.getQValue();
		float q2 = o.contactHeader.getQValue();
		if (q1 > q2) {
			return 1;
		} else if (q2 > q1) {
			return -1;
		} else {
			return 0;
		}
	}

	public ContactHeader getContactHeader() {
		return contactHeader;
	}

	public String getCallId() {
		return callId;
	}

	public long getCseq() {
		return cseq;
	}
}
