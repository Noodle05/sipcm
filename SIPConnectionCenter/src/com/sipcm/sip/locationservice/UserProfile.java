/**
 * 
 */
package com.sipcm.sip.locationservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@Component("sipUserProfile")
@Scope("prototype")
public class UserProfile {
	private static final Logger logger = LoggerFactory
			.getLogger(UserProfile.class);

	@Resource(name = "sipUtil")
	private SipUtil sipUtil;

	private String addressOfRecord;

	private UserSipProfile userSipProfile;

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
	 * @param userSipProfile
	 *            the userSipProfile to set
	 */
	public void setUserSipProfile(UserSipProfile userSipProfile) {
		this.userSipProfile = userSipProfile;
	}

	/**
	 * @return the userSipProfile
	 */
	public UserSipProfile getUserSipProfile() {
		return userSipProfile;
	}

	/**
	 * @param addressOfRecord
	 *            the addressOfRecord to set
	 */
	public void setAddressOfRecord(String addressOfRecord) {
		this.addressOfRecord = addressOfRecord;
	}

	/**
	 * @return the addressOfRecord
	 */
	public String getAddressOfRecord() {
		return addressOfRecord;
	}

	public Binding getBinding(Address address) {
		URI uri = sipUtil.getCanonicalizedURI(address.getURI());
		bindingsReadLock.lock();
		try {
			for (Binding binding : bindings) {
				Address a = binding.getAddress();
				URI u = sipUtil.getCanonicalizedURI(a.getURI());
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

	public Collection<Address> getAddresses() {
		Collection<Address> contactHeaders = new ArrayList<Address>(
				bindings.size());
		bindingsReadLock.lock();
		try {
			for (Binding binding : bindings) {
				contactHeaders.add(binding.getAddress());
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

	public void updateBinding(Address address, Address remoteEnd, String callId) {
		URI uri = sipUtil.getCanonicalizedURI(address.getURI());
		Binding binding = new Binding(address, remoteEnd, callId);
		bindingsReadLock.lock();
		try {
			Binding existingBinding = null;
			for (Binding b : bindings) {
				Address ch = b.getAddress();
				URI u = sipUtil.getCanonicalizedURI(ch.getURI());
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

	public void checkContactExpires() {
		bindingsWriteLock.lock();
		try {
			Iterator<Binding> ite = bindings.iterator();
			while (ite.hasNext()) {
				Binding binding = ite.next();
				binding.onContactExpire();
				if (binding.getAddress().getExpires() < 0) {
					if (logger.isInfoEnabled()) {
						logger.info("Contact {} expired, remove it.",
								binding.getAddress());
					}
					ite.remove();
				}
			}
		} finally {
			bindingsWriteLock.unlock();
		}
	}

	public boolean isEmpty() {
		bindingsReadLock.lock();
		try {
			return bindings.isEmpty();
		} finally {
			bindingsReadLock.unlock();
		}
	}
}
