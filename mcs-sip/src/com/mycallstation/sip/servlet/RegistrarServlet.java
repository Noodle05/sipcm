/**
 * 
 */
package com.mycallstation.sip.servlet;

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
import javax.sip.header.MinExpiresHeader;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.sip.locationservice.LocationService;
import com.mycallstation.sip.locationservice.RegisterTooFrequentException;

/**
 * @author Wei Gao
 * 
 */
@SipServlet(name = "RegistrarServlet", applicationName = "com.mycallstation.CallCenter", loadOnStartup = 1)
public class RegistrarServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 2954502051478832933L;

	@Resource(name = "sipLocationService")
	private LocationService locationService;

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
			if (!host.toUpperCase().endsWith(
					appConfig.getDomain().toUpperCase())) {
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
				if (expiresTime < appConfig.getSipMinExpires()) {
					SipServletResponse resp = req
							.createResponse(SipServletResponse.SC_INTERVAL_TOO_BRIEF);
					resp.addHeader(MinExpiresHeader.NAME,
							Integer.toString(appConfig.getSipMinExpires()));
					resp.send();
					return;
				}
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
					expiresTime = appConfig.getSipMaxExpires();
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
						if (contactExpiresTime < appConfig.getSipMinExpires()) {
							SipServletResponse resp = req
									.createResponse(SipServletResponse.SC_INTERVAL_TOO_BRIEF);
							resp.addHeader(MinExpiresHeader.NAME, Integer
									.toString(appConfig.getSipMinExpires()));
							resp.send();
							return;
						}
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
					try {
						locationService.updateRegistration(userSipProfile, b,
								contactExpiresTime, remoteEnd, callId);
					} catch (RegisterTooFrequentException e) {
						if (logger.isDebugEnabled()) {
							logger.debug(
									"This user register too frequently. Request: \"{}\"",
									req);
						}
						responseWithContact(userSipProfile, req,
								SipServletResponse.SC_BUSY_HERE,
								"Register too frequent, try later.");
						return;
					}
				}
			}
		}
		responseWithContact(userSipProfile, req, SipServletResponse.SC_OK);
	}

	private void responseWithContact(UserSipProfile userSipProfile,
			SipServletRequest req, int statuscode) throws IOException {
		responseWithContact(userSipProfile, req, statuscode, null);
	}

	private void responseWithContact(UserSipProfile userSipProfile,
			SipServletRequest req, int statuscode, String reasonPhrase)
			throws IOException {
		SipServletResponse resp = req.createResponse(statuscode, reasonPhrase);
		Collection<Address> contacts = locationService
				.getAddresses(userSipProfile);
		if (logger.isTraceEnabled()) {
			logger.trace("After register, contacts still contains:");
			for (Address a : contacts) {
				logger.trace("\t{}", a);
			}
		}
		boolean first = true;
		for (Address c : contacts) {
			resp.addAddressHeader(ContactHeader.NAME, c, first);
			if (first) {
				first = false;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Sending response: {}", resp);
		}
		resp.send();
	}

	private int correctExpiresTime(int expiresTime) {
		if (expiresTime != 0) {
			expiresTime = Math.min(expiresTime, appConfig.getSipMaxExpires());
			expiresTime = Math.max(expiresTime, appConfig.getSipMinExpires());
		}
		return expiresTime;
	}
}
