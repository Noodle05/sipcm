/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.common.model.User;
import com.sipcm.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@Component("sipUserProfile")
@Scope("prototype")
public class UserProfile {
	@Resource(name = "sipUtil")
	private SipUtil sipUtil;

	private SipURI addressOfRecord;

	private User user;

	private List<Binding> bindings;

	private Lock bindingsReadLock;

	private Lock bindingsWriteLock;

	@PostConstruct
	public void init() {
		bindings = new ArrayList<Binding>();
		ReadWriteLock rwl = new ReentrantReadWriteLock();
		bindingsReadLock = rwl.readLock();
		bindingsWriteLock = rwl.writeLock();
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param addressOfRecord
	 *            the addressOfRecord to set
	 */
	public void setAddressOfRecord(SipURI addressOfRecord) {
		this.addressOfRecord = addressOfRecord;
	}

	/**
	 * @return the addressOfRecord
	 */
	public SipURI getAddressOfRecord() {
		return addressOfRecord;
	}

	public Binding getBinding(ContactHeader contactHeader) {
		URI uri = sipUtil.getCanonicalizedURI(contactHeader.getAddress()
				.getURI());
		bindingsReadLock.lock();
		try {
			for (Binding binding : bindings) {
				ContactHeader ch = binding.getContactHeader();
				URI u = sipUtil.getCanonicalizedURI(ch.getAddress().getURI());
				if (uri.equals(u)) {
					return binding;
				}
			}
			return null;
		} finally {
			bindingsReadLock.unlock();
		}
	}

	public Binding removeBinding(Binding binding) {
		bindingsWriteLock.lock();
		try {
			if (bindings.remove(binding)) {
				return binding;
			} else {
				return null;
			}
		} finally {
			bindingsWriteLock.unlock();
		}
	}

	public void addBinding(Binding binding) {
		bindingsWriteLock.lock();
		try {
			bindings.add(binding);
			Collections.sort(bindings);
		} finally {
			bindingsWriteLock.unlock();
		}
	}

	public Collection<ContactHeader> getContactHeaders() {
		Collection<ContactHeader> contactHeaders = new ArrayList<ContactHeader>(
				bindings.size());
		bindingsReadLock.lock();
		try {
			for (Binding binding : bindings) {
				contactHeaders.add(binding.getContactHeader());
			}
		} finally {
			bindingsReadLock.unlock();
		}
		return contactHeaders;
	}

	public boolean hasNoBinding() {
		bindingsReadLock.lock();
		try {
			return bindings.isEmpty();
		} finally {
			bindingsReadLock.unlock();
		}
	}

	public void updateBinding(ContactHeader contactHeader, String callId,
			long cseq) {
		URI uri = sipUtil.getCanonicalizedURI(contactHeader.getAddress()
				.getURI());
		Binding binding = new Binding(contactHeader, callId, cseq);
		bindingsReadLock.lock();
		try {
			Binding existingBinding = null;
			for (Binding b : bindings) {
				ContactHeader ch = b.getContactHeader();
				URI u = sipUtil.getCanonicalizedURI(ch.getAddress().getURI());
				if (uri.equals(u)) {
					existingBinding = b;
					break;
				}
			}
			bindingsReadLock.unlock();
			bindingsWriteLock.lock();
			try {
				if (existingBinding != null) {
					bindings.remove(existingBinding);
				}
				bindings.add(binding);
				Collections.sort(bindings);
			} finally {
				bindingsReadLock.lock();
				bindingsWriteLock.unlock();
			}
		} finally {
			bindingsReadLock.unlock();
		}
	}
}
