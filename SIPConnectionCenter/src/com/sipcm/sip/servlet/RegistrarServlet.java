/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.header.ContactHeader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.sip.locationservice.Binding;
import com.sipcm.sip.locationservice.LocationService;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "RegistrarServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class RegistrarServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 2954502051478832933L;

	public static final String SIP_MIN_EXPIRESTIME = "sip.expirestime.min";
	public static final String SIP_MAX_EXPIRESTIME = "sip.expirestime.max";

	@Autowired
	@Qualifier("sipLocationService")
	private LocationService locationService;

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doRegister(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doRegister(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get registration request {}", req);
		}
		try {
			processRegister(req);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new ServletException("Error happened during registration.", e);
		}
	}

	private void processRegister(SipServletRequest req) throws Exception {
		User user = (User)req.getAttribute(USER_ATTRIBUTE);
		URI toURI = req.getTo().getURI();
		if (toURI.isSipURI()) {
			final SipURI sipUri = (SipURI) toURI;
			String host = sipUri.getHost();
			if (!getDomain().equalsIgnoreCase(host)) {
				SipServletResponse response = req.createResponse(
						SipServletResponse.SC_FORBIDDEN,
						"Do not serve your domain.");
				response.send();
				return;
			}
			if (user == null) {
				String username = sipUri.getUser();
				user = userService.getUserByUsername(username);
			}
			toURI = sipFactory.createSipURI(user.getUsername(), sipUri.getHost());
		} else {
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_UNSUPPORTED_URI_SCHEME);
			response.send();
			return;
		}
		String key = toURI.toString();
		if (logger.isTraceEnabled()) {
			logger.trace("Lookup based on key: {}", key);
		}
		Iterator<Address> ite = req.getAddressHeaders(ContactHeader.NAME);
		Collection<Address> contacts = new ArrayList<Address>();
		boolean wildChar = false;
		while (ite.hasNext()) {
			Address a = ite.next();
			contacts.add(a);
			if (a.isWildcard()) {
				wildChar = true;
			}
		}

		if (!contacts.isEmpty()) {
			int expiresTime = req.getExpires();
			if (expiresTime > 0) {
				expiresTime = correctExpiresTime(expiresTime);
			}
			if (wildChar) {
				if (contacts.size() > 1 || expiresTime != 0) {
					SipServletResponse response = req
							.createResponse(SipServletResponse.SC_BAD_REQUEST);
					response.send();
					return;
				}
				locationService.removeAllBinding(key);
			} else {
				if (expiresTime <= 0) {
					expiresTime = appConfig.getInt(SIP_MAX_EXPIRESTIME);
				}
				for (Address a : contacts) {
					if (logger.isTraceEnabled()) {
						logger.trace("Processing address: {}", a);
					}
					int contactExpiresTime = a.getExpires();
					if (contactExpiresTime < 0) {
						contactExpiresTime = expiresTime;
					}
					if (contactExpiresTime > 0) {
						contactExpiresTime = correctExpiresTime(contactExpiresTime);
					}
					if (logger.isTraceEnabled()) {
						logger.trace("Expirestime: {}", contactExpiresTime);
					}
					a.setExpires(contactExpiresTime);

					Binding existingBinding = locationService
							.getBinding(key, a);
					String callId = req.getCallId();
					if (existingBinding != null) {
						if (logger.isTraceEnabled()) {
							logger.trace(
									"Find existing binding, will update it. Bind: {}",
									existingBinding);
						}
						if (a.getExpires() == 0) {
							if (logger.isTraceEnabled()) {
								logger.trace("Remove addess {}", a);
							}
							locationService.removeBinding(key, a);
						} else {
							if (logger.isTraceEnabled()) {
								logger.trace("Update address addess {}", a);
							}
							locationService.updateRegistration(key, a, callId);
						}
					} else {
						if (a.getExpires() > 0) {
							if (logger.isTraceEnabled()) {
								logger.trace("Add address {}", a);
							}
							locationService.register(key, user, a, callId);
						}
					}
				}
			}
		}
		contacts = locationService.getAddresses(key);
		if (logger.isTraceEnabled()) {
			logger.trace("After register, contacts still contains:");
			for (Address a : contacts) {
				logger.trace("\t{}", a);
			}
		}
		SipServletResponse response = req
				.createResponse(SipServletResponse.SC_OK);
		boolean first = true;
		for (Address c : contacts) {
			response.addAddressHeader(ContactHeader.NAME, c, first);
			if (first) {
				first = false;
			}
		}
		response.send();
	}

	private int correctExpiresTime(int expiresTime) {
		if (expiresTime != 0) {
			expiresTime = Math.min(expiresTime,
					appConfig.getInt(SIP_MAX_EXPIRESTIME, 3600));
			expiresTime = Math.max(expiresTime,
					appConfig.getInt(SIP_MIN_EXPIRESTIME, 300));
		}
		return expiresTime;
	}
}
