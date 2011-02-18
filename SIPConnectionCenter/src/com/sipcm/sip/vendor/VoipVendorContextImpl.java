/**
 * 
 */
package com.sipcm.sip.vendor;

import gov.nist.javax.sip.message.SIPRequest;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
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
@Scope("prototype")
public class VoipVendorContextImpl extends VoipLocalVendorContextImpl {
	public static final String REGISTER_EXPIRES = "sip.client.register.expires";
	private static final String REGISTER_ALLOW_METHODS = "sip.client.register.allow.methods";

	@Resource(name = "userVoidAccountService")
	private UserVoipAccountService userVoipAccountService;

	@Resource(name = "global.scheduler")
	private TaskScheduler taskScheduler;

	private ScheduledFuture<?> renewFuture;

	private final RegisterRenewTask renewTask;

	private String allowMethods;

	private int expires;

	public VoipVendorContextImpl() {
		renewTask = new RegisterRenewTask(this);
	}

	@PostConstruct
	public void init() {
		allowMethods = getAllowMethods();
		expires = getRegisterExpries();
		scheduleRenewTask();
	}

	@PreDestroy
	public void destroy() {
		cancelRenewTask();
	}

	private void scheduleRenewTask() {
		cancelRenewTask();
		long period = (expires - 30) * 1000L;
		if (logger.isDebugEnabled()) {
			logger.debug("Scheduling renew register task in period: {}ms",
					period);
		}
		renewFuture = taskScheduler.scheduleAtFixedRate(renewTask, period);
	}

	private void cancelRenewTask() {
		if (renewFuture != null && !renewFuture.isCancelled()
				&& !renewFuture.isDone()) {
			renewFuture.cancel(false);
			renewFuture = null;
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
				SipServletRequest register = generateRegisterRequest(null,
						account, getRegisterExpries());

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

	private SipServletRequest generateRegisterRequest(
			SipApplicationSession appSession, UserVoipAccount account,
			int expires) throws ServletParseException {
		SipFactory sipFactory = voipVendorManager.getSipFactory();
		String contactHost = voipVendorManager.getContactHost();
		if (appSession == null) {
			appSession = sipFactory.createApplicationSession();
		}
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
			if (logger.isTraceEnabled()) {
				logger.trace("Viop Vendor: {}, Proxy: {}", voipVendor,
						voipVendor.getProxy());
			}
			Address routeAddress = sipFactory.createAddress("sip:"
					+ voipVendor.getProxy());
			register.pushRoute(routeAddress);
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
		if (logger.isDebugEnabled()) {
			logger.debug("Get response: {}", resp);
		}
		if (resp.getStatus() == SipServletResponse.SC_UNAUTHORIZED
				|| resp.getStatus() == SipServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED) {
			processAuthInfo(resp, account);
		} else if (resp.getStatus() == SipServletResponse.SC_OK) {
			int e = resp.getExpires();
			String ch = resp.getHeader(ContactHeader.NAME);
			if (ch != null) {
				Address a = voipVendorManager.getSipFactory().createAddress(ch);
				if (a.getExpires() > 0) {
					e = a.getExpires();
				}
			}
			if (e <= 0) {
				account.setOnline(false);
			} else {
				account.setOnline(true);
				if (e < expires) {
					expires = e;
					scheduleRenewTask();
				}
			}
			userVoipAccountService.updateOnlineStatus(account);
			SipApplicationSession appSession = resp
					.getApplicationSession(false);
			if (appSession != null) {
				appSession.invalidate();
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Register account \"{}\" failed, response is: {}",
						account, resp);
			}
			account.setOnline(false);
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
			SipServletRequest req = generateRegisterRequest(appSession,
					account, expires);
			req.addAuthHeader(resp, account.getAccount(), account.getPassword());
			if (logger.isTraceEnabled()) {
				logger.trace("Sending challenge request {}", req);
			}
			req.send();
		} else {
			appSession.invalidate();
			if (logger.isWarnEnabled()) {
				logger.warn("Authentication failed for account: \"{}\"",
						account);
			}
			account.setOnline(false);
			userVoipAccountService.updateOnlineStatus(account);
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
				SipServletRequest register = generateRegisterRequest(null,
						account, 0);

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

	private void renewRegister() {
		if (logger.isDebugEnabled()) {
			logger.debug("Timeout, renew all registered accounts.");
		}
		Collection<UserVoipAccount> accounts = userVoipAccountService
				.getOnlineIncomingAccounts(voipVendor);
		if (accounts != null && !accounts.isEmpty()) {
			for (UserVoipAccount account : accounts) {
				registerForIncomingRequest(account);
			}
		}
	}

	@Override
	public String toString() {
		return "Voip Vendor Context object for " + voipVendor;
	}

	private static class RegisterRenewTask implements Runnable {
		private final VoipVendorContextImpl ctx;

		public RegisterRenewTask(VoipVendorContextImpl ctx) {
			this.ctx = ctx;
		}

		@Override
		public void run() {
			ctx.renewRegister();
		}
	}
}
