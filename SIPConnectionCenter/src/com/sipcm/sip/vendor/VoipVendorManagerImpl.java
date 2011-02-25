/**
 * 
 */
package com.sipcm.sip.vendor;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import com.sipcm.sip.business.UserVoipAccountService;
import com.sipcm.sip.business.VoipVendorService;
import com.sipcm.sip.locationservice.UserBindingInfo;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.model.VoipVendor;
import com.sipcm.sip.nat.PublicIpAddressHolder;
import com.sipcm.sip.servlet.AbstractSipServlet;

/**
 * @author wgao
 * 
 */
public abstract class VoipVendorManagerImpl implements VoipVendorManager,
		ServletContextAware {
	private static final Logger logger = LoggerFactory
			.getLogger(VoipVendorManagerImpl.class);

	private final ConcurrentMap<VoipVendor, VoipVendorContext> voipVendors;

	@Resource(name = "voipVendorService")
	private VoipVendorService voipVendorService;

	@Resource(name = "userVoidAccountService")
	private UserVoipAccountService userVoipAccountService;

	@Resource(name = "publicIpAddressHolder")
	private PublicIpAddressHolder publicIpAddressHolder;

	private SipFactory sipFactory;
	private List<String> supportedMethods;
	private String contactHost;

	public VoipVendorManagerImpl() {
		voipVendors = new ConcurrentHashMap<VoipVendor, VoipVendorContext>();
	}

	protected abstract VoipVendorContext createSipVoipVendorContext();

	protected abstract VoipVendorContext createLocalVoipVendorContext();

	private VoipVendorContext createVoipVendorContext(VoipVendor voipVendor) {
		VoipVendorContext ctx = null;
		try {
			switch (voipVendor.getType()) {
			case SIP:
				ctx = createSipVoipVendorContext();
				break;
			case LOCAL:
				ctx = createLocalVoipVendorContext();
				break;
			default:
				ctx = null;
				break;
			}
			if (ctx != null) {
				ctx.initialize(voipVendor);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"Error happened when creating context object for voip vendor: "
								+ voipVendor, e);
			}
			ctx = null;
		}
		return ctx;
	}

	@PostConstruct
	public void init() {

		List<VoipVendor> venders = voipVendorService.getEntities();
		for (VoipVendor vender : venders) {
			VoipVendorContext ctx = createVoipVendorContext(vender);
			if (ctx != null) {
				voipVendors.put(vender, ctx);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Initial voip vendor context done.");
			for (Entry<VoipVendor, VoipVendorContext> entry : voipVendors
					.entrySet()) {
				logger.trace("{}: {}", entry.getKey(), entry.getValue());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorManager#registerForIncomingRequest(com
	 * .sipcm.sip.model.UserSipProfile)
	 */
	@Override
	public void registerForIncomingRequest(UserSipProfile userSipProfile) {
		Collection<UserVoipAccount> accounts = userVoipAccountService
				.getIncomingAccounts(userSipProfile);
		if (accounts != null && !accounts.isEmpty()) {
			for (UserVoipAccount account : accounts) {
				VoipVendorContext ctx = getVoipVendorContext(account);
				if (ctx != null) {
					ctx.registerForIncomingRequest(account);
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn(
								"Cannot find vendor context for vendor \"{}\"",
								account.getVoipVendor());
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.vendor.VoipVendorManager#unregisterForIncomingRequest(com
	 * .sipcm.sip.model.UserSipProfile)
	 */
	@Override
	public void unregisterForIncomingRequest(UserSipProfile userSipProfile) {
		Collection<UserVoipAccount> accounts = userVoipAccountService
				.getOnlineIncomingAccounts(userSipProfile);
		if (accounts != null && !accounts.isEmpty()) {
			for (UserVoipAccount account : accounts) {
				VoipVendorContext ctx = getVoipVendorContext(account);
				if (ctx != null) {
					ctx.unregisterForIncomingRequest(account);
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn(
								"Cannot find vendor context for vendor \"{}\"",
								account.getVoipVendor());
					}
				}
			}
		}
	}

	private VoipVendorContext getVoipVendorContext(UserVoipAccount account) {
		VoipVendorContext ctx = voipVendors.get(account.getVoipVendor());
		if (logger.isTraceEnabled()) {
			logger.trace("VoipVendorContext for account \"{}\" is \"{}\"",
					account, ctx);
		}
		return ctx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorManager#onUserDeleted(java.lang.Long)
	 */
	@Override
	public void onUserDeleted(Long... userIds) {
		for (Long id : userIds) {
			Collection<UserVoipAccount> accounts = userVoipAccountService
					.getOnlineIncomingAccounts(id);
			if (accounts != null && !accounts.isEmpty()) {
				for (UserVoipAccount account : accounts) {
					VoipVendorContext ctx = getVoipVendorContext(account);
					if (ctx != null) {
						ctx.unregisterForIncomingRequest(account);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorManager#isLocalUsr(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public UserBindingInfo isLocalUsr(String toHost, String toUser) {
		for (Entry<VoipVendor, VoipVendorContext> entry : voipVendors
				.entrySet()) {
			VoipVendor vendor = entry.getKey();
			if (toHost.toUpperCase().endsWith(vendor.getDomain().toUpperCase())) {
				VoipVendorContext ctx = entry.getValue();
				return ctx.isLocalUser(toUser);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.context.ServletContextAware#setServletContext
	 * (javax.servlet.ServletContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setServletContext(ServletContext servletContext) {
		sipFactory = (SipFactory) servletContext
				.getAttribute(SipServlet.SIP_FACTORY);
		supportedMethods = (List<String>) servletContext
				.getAttribute(SipServlet.SUPPORTED);
		if (logger.isInfoEnabled()) {
			if (supportedMethods != null) {
				logger.info("Supported methods: {}", supportedMethods);
			} else {
				logger.info("Didn't found supported methods.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.vendor.VoipVendorManager#setListeningAddress(java.net.
	 * InetAddress, int)
	 */
	@Override
	public void setListeningAddress(InetAddress listeningIp, int listeningPort) {
		if (publicIpAddressHolder.getPublicIp() != null) {
			contactHost = publicIpAddressHolder.getPublicIp().getHostAddress()
					+ ":" + listeningPort;
		} else if (listeningIp != null) {
			contactHost = listeningIp.getHostAddress() + ":" + listeningPort;
		}
		if (logger.isInfoEnabled()) {
			logger.info(
					"Outgoing register request will use \"{}\" as contact host.",
					contactHost);
		}
	}

	@Override
	public SipFactory getSipFactory() {
		return sipFactory;
	}

	@Override
	public List<String> getSupportedMethods() {
		return supportedMethods;
	}

	@Override
	public String getContactHost() {
		return contactHost;
	}

	@Override
	public void handleRegisterResponse(SipServletResponse resp)
			throws ServletException, IOException {
		SipApplicationSession appSession = resp.getApplicationSession(false);
		if (appSession != null) {
			UserVoipAccount account = (UserVoipAccount) appSession
					.getAttribute(AbstractSipServlet.USER_VOIP_ACCOUNT);
			if (account != null) {
				VoipVendorContext ctx = getVoipVendorContext(account);
				if (ctx != null) {
					ctx.handleRegisterResponse(resp, account);
				}
			}
		}
	}
}
