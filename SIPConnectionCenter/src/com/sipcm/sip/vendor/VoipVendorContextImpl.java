/**
 * 
 */
package com.sipcm.sip.vendor;

import gov.nist.javax.sip.message.SIPRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.URI;
import javax.sip.header.AllowHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.RouteHeader;

import org.springframework.stereotype.Component;

import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.servlet.AbstractSipServlet;

/**
 * @author wgao
 * 
 */
@Component("sipVoipVendorContext")
public class VoipVendorContextImpl extends VoipLocalVendorContextImpl {
	public static final String REGISTER_EXPIRES = "sip.client.register.expires";
	private static final String REGISTER_ALLOW_METHODS = "sip.client.register.allow.methods";

	@Resource(name = "userVoidAccountService")
	private UserVoipAccountService userVoipAccountService;

	private ConcurrentMap<String, ClientRegisterHolder> cache;

	private String allowMethods;

	@PostConstruct
	public void init() {
		// cache = new MapMaker().concurrencyLevel(32).softValues()
		// .expiration(30, TimeUnit.MINUTES).makeMap();
		cache = new ConcurrentHashMap<String, ClientRegisterHolder>();
		allowMethods = getAllowMethods();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorContext#onUserDeleted(java.lang.Long)
	 */
	@Override
	public void onUserDeleted(Long... userIds) {
		List<Long> ids = Arrays.asList(userIds);
		Collections.sort(ids);
		Iterator<Entry<String, ClientRegisterHolder>> ite = cache.entrySet()
				.iterator();
		while (ite.hasNext() && !ids.isEmpty()) {
			Entry<String, ClientRegisterHolder> entry = ite.next();
			ClientRegisterHolder holder = entry.getValue();
			UserSipProfile userSipProfile = holder.getUserSipProfile();
			int index = Collections.binarySearch(ids, userSipProfile.getId());
			if (index >= 0) {
				ids.remove(index);
				ite.remove();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipLocalVendorContextImpl#registerForIncomingRequest
	 * (com.sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void registerForIncomingRequest(UserVoipAccount account) {
		String contactHost = voipVendorManager.getContactHost();
		if (contactHost != null) {
			try {
				SipServletRequest register = generateRegisterRequest(account,
						getRegisterExpries());

				if (logger.isTraceEnabled()) {
					logger.trace("Sending register request: {}", register);
				}
				register.send();
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Error happened when try to register account: "
							+ account, e);
				}
			}
		}
	}

	private SipServletRequest generateRegisterRequest(UserVoipAccount account,
			int expires) throws ServletParseException {
		SipFactory sipFactory = voipVendorManager.getSipFactory();
		String contactHost = voipVendorManager.getContactHost();
		SipApplicationSession appSession = sipFactory
				.createApplicationSession();
		appSession.setAttribute(AbstractSipServlet.USER_VOIP_ACCOUNT, account);
		// To/From header
		URI toURI = sipFactory.createSipURI(account.getAccount(),
				voipVendor.getDomain());
		SipServletRequest register = sipFactory.createRequest(appSession,
				SIPRequest.REGISTER, toURI, toURI);

		// Request URI
		URI domainURI = sipFactory.createSipURI(null, voipVendor.getDomain());
		register.setRequestURI(domainURI);

		URI contact = sipFactory
				.createSipURI(account.getAccount(), contactHost);
		Address ca = sipFactory.createAddress(contact, account.getOwner()
				.getDisplayName());
		ca.setExpires(expires);
		register.setAddressHeader(ContactHeader.NAME, ca);
		register.setExpires(expires);
		if (allowMethods != null) {
			register.setHeader(AllowHeader.NAME, allowMethods);
		}

		if (voipVendor.getProxy() != null) {
			Address routeAddress = sipFactory.createAddress("sip:"
					+ voipVendor.getProxy());
			register.setAddressHeader(RouteHeader.NAME, routeAddress);
		}
		return register;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipLocalVendorContextImpl#handleRegisterResponse
	 * (javax.servlet.sip.SipServletResponse,
	 * com.sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account) throws ServletException, IOException {
		if (resp.getStatus() == SipServletResponse.SC_UNAUTHORIZED
				|| resp.getStatus() == SipServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED) {
			if (logger.isDebugEnabled()) {
				logger.debug("Get response: {}", resp);
			}
			processAuthInfo(resp, account);
		} else if (resp.getStatus() == SipServletResponse.SC_OK) {
			account.setOnline(true);
			userVoipAccountService.updateOnlineStatus(account);
			SipApplicationSession appSession = resp
					.getApplicationSession(false);
			if (appSession != null) {
				appSession.invalidate();
			}
		}
	}

	private void processAuthInfo(javax.servlet.sip.SipServletResponse resp,
			UserVoipAccount account) throws javax.servlet.ServletException,
			java.io.IOException {
		SipApplicationSession appSession = resp.getApplicationSession();
		// Avoid re-sending if the auth repeatedly fails.
		if (!"true".equals(appSession.getAttribute("FirstResponseRecieved"))) {
			if (logger.isTraceEnabled()) {
				logger.trace("First try.");
			}
			appSession.setAttribute("FirstResponseRecieved", "true");
			SipServletRequest or = resp.getRequest();
			int expires = or.getExpires();
			SipServletRequest req = generateRegisterRequest(account, expires);
			req.addAuthHeader(resp, account.getAccount(), account.getPassword());
			if (logger.isTraceEnabled()) {
				logger.trace("Sending challenge request {}", req);
			}
			req.send();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipLocalVendorContextImpl#unregisterForIncomingRequest
	 * (com.sipcm.sip.model.UserVoipAccount)
	 */
	@Override
	public void unregisterForIncomingRequest(UserVoipAccount account) {
		String contactHost = voipVendorManager.getContactHost();
		if (contactHost != null) {
			try {
				SipServletRequest register = generateRegisterRequest(account, 0);

				if (logger.isTraceEnabled()) {
					logger.trace("Sending register request: {}", register);
				}
				register.send();
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Error happened when try to unregister account: "
									+ account, e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorContext#isLocalUser(java.lang.String)
	 */
	@Override
	public Collection<AddressBinding> isLocalUser(String toUser) {
		UserVoipAccount account = userVoipAccountService
				.getUserVoipAccountByVendorAndAccount(voipVendor, toUser);
		if (account != null) {
			UserSipProfile profile = account.getOwner();
			return locationService.getUserBinding(profile);
		}
		return null;
	}

	private int getRegisterExpries() {
		return appConfig.getInt(REGISTER_EXPIRES, 3600);
	}

	private String getAllowMethods() {
		String ret = null;
		String[] methods = appConfig.getStringArray(REGISTER_ALLOW_METHODS);
		if (methods != null && methods.length > 0) {
			for (String m : methods) {
				if (ret == null) {
					ret = m;
				} else {
					ret = ret + "," + m;
				}
			}
		}
		return ret;
	}
}
