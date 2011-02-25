/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.header.ContactHeader;

import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.model.UserSipProfile;

/**
 * @author wgao
 * 
 */
@SipServlet(name = "RegistrarServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class RegistrarServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 2954502051478832933L;

	public static final String SIP_MIN_EXPIRESTIME = "sip.expirestime.min";
	public static final String SIP_MAX_EXPIRESTIME = "sip.expirestime.max";

	@Resource(name = "sip.LocationService")
	private LocationService locationService;

	// @Override
	// public void init() throws ServletException {
	// super.init();
	// locationService = (LocationService) getServletContext().getAttribute(
	// "sip.LocationService");
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doResponse(javax.servlet.sip.SipServletResponse
	 * )
	 */
	@Override
	public void doResponse(SipServletResponse response)
			throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("I got response as: \"{}\"", response);
		}
		super.doResponse(response);
	}

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
		UserSipProfile userSipProfile = (UserSipProfile) req
				.getAttribute(USER_ATTRIBUTE);
		URI toURI = req.getTo().getURI();
		if (toURI.isSipURI()) {
			final SipURI sipUri = (SipURI) toURI;
			String host = sipUri.getHost();
			if (!host.toUpperCase().endsWith(getDomain().toUpperCase())) {
				SipServletResponse response = req.createResponse(
						SipServletResponse.SC_FORBIDDEN,
						"Do not serve your domain.");
				response.send();
				return;
			}
			if (userSipProfile == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("This shouldn't happen. How come no user sip profile found during registeration?");
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			if (!userSipProfile.getOwner().getUsername()
					.equalsIgnoreCase(sipUri.getUser())) {
				if (logger.isInfoEnabled()) {
					logger.info(
							"Attack detected, \"{}\" is trying to register as \"{}\"",
							userSipProfile.getDisplayName(), sipUri.getUser());
				}
				response(req, SipServletResponse.SC_BAD_REQUEST);
				return;
			}
		} else {
			response(req, SipServletResponse.SC_UNSUPPORTED_URI_SCHEME);
			return;
		}
		// if (logger.isTraceEnabled()) {
		// logger.trace("Lookup based on key: {}", key);
		// }
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
			String rmaddr = req.getInitialRemoteAddr();
			int rport = req.getInitialRemotePort();
			String rt = req.getInitialTransport();

			int expiresTime = req.getExpires();
			if (expiresTime > 0) {
				expiresTime = correctExpiresTime(expiresTime);
			}
			if (wildChar) {
				if (contacts.size() > 1 || expiresTime != 0) {
					response(req, SipServletResponse.SC_BAD_REQUEST);
					return;
				}
				locationService.removeAllBinding(userSipProfile);
			} else {
				if (expiresTime < 0) {
					expiresTime = appConfig.getInt(SIP_MAX_EXPIRESTIME);
				}
				for (Address a : contacts) {
					if (logger.isTraceEnabled()) {
						logger.trace("Processing address: {}", a);
					}
					Address b = (Address) a.clone();
					Iterator<String> pns = a.getParameterNames();
					while (pns.hasNext()) {
						b.removeParameter(pns.next());
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
					Address remoteEnd = sipFactory.createAddress(b.getURI()
							.clone());
					URI ruri = remoteEnd.getURI();
					if (ruri.isSipURI()) {
						final SipURI sruri = (SipURI) ruri;
						sruri.setHost(rmaddr);
						sruri.setTransportParam(rt);
						sruri.setPort(rport);
						remoteEnd.setURI(sruri);
					}

					String callId = req.getCallId();
					locationService.updateRegistration(userSipProfile, b,
							contactExpiresTime, remoteEnd, callId);
				}
			}
		}
		contacts = locationService.getAddresses(userSipProfile);
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
		if (logger.isDebugEnabled()) {
			logger.debug("Sending response {}", response);
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
