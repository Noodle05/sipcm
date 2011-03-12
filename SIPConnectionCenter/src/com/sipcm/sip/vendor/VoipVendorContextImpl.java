/**
 * 
 */
package com.sipcm.sip.vendor;

import gov.nist.javax.sip.message.SIPRequest;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

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

import org.mobicents.servlet.sip.message.SipServletResponseImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.locationservice.UserBindingInfo;
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
	@Resource(name = "userVoipAccountService")
	private UserVoipAccountService userVoipAccountService;

	private String allowMethods;

	private int expires;

	@PostConstruct
	public void init() {
		allowMethods = appConfig.getSipClientAllowMethods();
		expires = appConfig.getSipClientRegisterExpries();
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
				if (account.getAuthResponse() != null) {
					handleRegisterResponse(account.getAuthResponse(), account,
							getExpires(), true);
				} else {
					SipServletRequest register = generateRegisterRequest(null,
							account, getExpires());

					if (logger.isTraceEnabled()) {
						logger.trace("Sending register request: {}", register);
					}
					register.send();
				}
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

		Address ca = generateContact(account);
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

	private Address generateContact(UserVoipAccount account) {
		SipFactory sipFactory = voipVendorManager.getSipFactory();
		String contactHost = voipVendorManager.getContactHost();
		URI contact = sipFactory
				.createSipURI(account.getAccount(), contactHost);
		Address ca = sipFactory.createAddress(contact, account.getOwner()
				.getDisplayName());
		return ca;
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
		handleRegisterResponse(resp, account, getExpires(), false);
	}

	private void handleRegisterResponse(SipServletResponse resp,
			UserVoipAccount account, int expires, boolean withResponse)
			throws ServletException, IOException {
		if (!withResponse) {
			if (logger.isDebugEnabled()) {
				logger.debug("Get response: {}", resp);
			}
		}
		if (resp.getStatus() == SipServletResponse.SC_UNAUTHORIZED
				|| resp.getStatus() == SipServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED) {
			processAuthInfo(resp, account, expires, withResponse);
		} else if (resp.getStatus() == SipServletResponse.SC_OK) {
			Address ca = generateContact(account);
			ca.setExpires(0);
			int e = resp.getExpires();
			Iterator<String> ite = resp.getHeaders(ContactHeader.NAME);
			while (ite.hasNext()) {
				String c = ite.next();
				Address a = voipVendorManager.getSipFactory().createAddress(c);
				int b = a.getExpires();
				a.setExpires(0);
				if (ca.equals(a)) {
					e = b;
					break;
				}
			}
			if (e <= 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("{} deregistered.", account);
				}
				account.setAuthResponse(null);
				account.setRegExpires(null);
				account.setLastCheck(null);
				account.setErrorCode(0);
				account.setErrorMessage(null);
				userVoipAccountService
						.updateRegisterExpiresAndAuthResonse(account);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("{} registered.", account);
				}
				account.setRegExpires(e);
				account.setLastCheck((int) (System.currentTimeMillis() / 1000L));
				account.setErrorCode(0);
				account.setErrorMessage(null);
				userVoipAccountService.updateRegisterExpires(account);
			}
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
			account.setRegExpires(null);
			account.setAuthResponse(null);
			account.setErrorCode(resp.getStatus());
			account.setErrorMessage(resp.getReasonPhrase());
			userVoipAccountService.updateRegisterExpiresAndAuthResonse(account);
			SipApplicationSession appSession = resp
					.getApplicationSession(false);
			if (appSession != null) {
				appSession.invalidate();
			}
		}
	}

	private void processAuthInfo(javax.servlet.sip.SipServletResponse resp,
			UserVoipAccount account, int expires, boolean withResponse)
			throws javax.servlet.ServletException, java.io.IOException {
		SipApplicationSession appSession = resp.getApplicationSession();
		// Avoid re-sending if the auth repeatedly fails.
		if (!"true".equals(appSession.getAttribute("FirstResponseRecieved"))) {
			if (logger.isTraceEnabled()) {
				logger.trace("First try.");
			}
			if (!withResponse) {
				account.setAuthResponse((SipServletResponseImpl) resp);
				userVoipAccountService.updateAuthResponse(account);
				appSession.setAttribute("FirstResponseRecieved", "true");
			}
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
			account.setAuthResponse(null);
			account.setRegExpires(null);
			account.setLastCheck(null);
			account.setErrorCode(resp.getStatus());
			account.setErrorMessage(resp.getReasonPhrase());
			userVoipAccountService.updateRegisterExpiresAndAuthResonse(account);
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
				if (account.getAuthResponse() != null) {
					handleRegisterResponse(account.getAuthResponse(), account,
							0, true);
				} else {
					SipServletRequest register = generateRegisterRequest(null,
							account, 0);

					if (logger.isTraceEnabled()) {
						logger.trace("Sending register request: {}", register);
					}
					register.send();
				}
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
	public UserBindingInfo isLocalUser(String toUser) {
		UserVoipAccount account = userVoipAccountService
				.getUserVoipAccountByVendorAndAccount(voipVendor, toUser);
		if (account != null) {
			UserSipProfile profile = account.getOwner();
			Collection<AddressBinding> abs = locationService
					.getUserBinding(profile);
			if (abs != null && !abs.isEmpty()) {
				return new UserBindingInfo(account, abs);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipLocalVendorContextImpl#toString()
	 */
	@Override
	public String toString() {
		return "Voip Vendor Context object for " + voipVendor;
	}

	private int getExpires() {
		if (voipVendor.getDefaultExpires() != null) {
			return voipVendor.getDefaultExpires();
		} else {
			return expires;
		}
	}
}
