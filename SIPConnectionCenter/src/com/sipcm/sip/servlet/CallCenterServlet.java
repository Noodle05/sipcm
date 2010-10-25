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
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.sip.header.ContactHeader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.sip.locationservice.Binding;
import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.locationservice.UserNotFoundException;
import com.sipcm.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@Configurable
public class CallCenterServlet extends AbstractSipServlet {
	private static final long serialVersionUID = -4659953196279153841L;

	public static final String SIP_MIN_EXPIRESTIME = "sip.expirestime.min";
	public static final String SIP_MAX_EXPIRESTIME = "sip.expirestime.max";

	@Autowired
	@Qualifier("sipLocationService")
	private LocationService locationService;

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("sipUtil")
	private SipUtil sipUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doRegister(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	public void doRegister(SipServletRequest req) throws ServletException,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doInvite(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	public void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get invite request: {}", req);
		}
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
			toURI = sipFactory.createSipURI(sipUri.getUser(), sipUri.getHost());
			Collection<Address> addresses = null;
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Lookup address with key: {}", toURI);
				}
				addresses = locationService.getAddresses(toURI.toString());
				if (logger.isTraceEnabled()) {
					logger.trace("Lookup result: ");
					for (Address a : addresses) {
						logger.trace("\t{}", a);
					}
				}
			} catch (UserNotFoundException e) {
				SipServletResponse response = req
						.createResponse(SipServletResponse.SC_NOT_FOUND);
				response.send();
				return;
			}
			if (addresses != null && !addresses.isEmpty()) {
				Address address = addresses.iterator().next();
				B2buaHelper helper = req.getB2buaHelper();
				SipServletRequest forkedRequest = helper.createRequest(req);
				forkedRequest.setRequestURI(sipUtil.getCanonicalizedURI(address
						.getURI()));
				forkedRequest.getSession().setAttribute("originalRequest", req);
				forkedRequest.send();
			} else {
				SipServletResponse response = req
						.createResponse(SipServletResponse.SC_NOT_FOUND);
				response.send();
				return;
			}
		} else {
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_UNSUPPORTED_URI_SCHEME);
			response.send();
			return;
		}
	}

	@Override
	protected void doBye(SipServletRequest request) throws ServletException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got BYE: " + request.toString());
		}
		// we send the OK directly to the first call leg
		SipServletResponse sipServletResponse = request
				.createResponse(SipServletResponse.SC_OK);
		sipServletResponse.send();

		// we forward the BYE
		SipSession session = request.getSession();
		B2buaHelper helper = request.getB2buaHelper();
		SipSession linkedSession = helper.getLinkedSession(session);
		SipServletRequest forkedRequest = linkedSession.createRequest("BYE");
		if (logger.isInfoEnabled()) {
			logger.info("forkedRequest = " + forkedRequest);
		}
		forkedRequest.send();
		if (session != null && session.isValid()) {
			session.invalidate();
		}
		return;
	}

	@Override
	protected void doUpdate(SipServletRequest request) throws ServletException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got UPDATE: " + request.toString());
		}
		B2buaHelper helper = request.getB2buaHelper();
		SipSession peerSession = helper.getLinkedSession(request.getSession());
		SipServletRequest update = helper.createRequest(peerSession, request,
				null);
		update.send();
	}

	@Override
	protected void doCancel(SipServletRequest request) throws ServletException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got CANCEL: " + request.toString());
		}
		SipSession session = request.getSession();
		B2buaHelper helper = request.getB2buaHelper();
		SipSession linkedSession = helper.getLinkedSession(session);
		SipServletRequest originalRequest = (SipServletRequest) linkedSession
				.getAttribute("originalRequest");
		SipServletRequest cancelRequest = helper.getLinkedSipServletRequest(
				originalRequest).createCancel();
		if (logger.isInfoEnabled()) {
			logger.info("forkedRequest = " + cancelRequest);
		}
		cancelRequest.send();
	}

	@Override
	protected void doSuccessResponse(SipServletResponse sipServletResponse)
			throws ServletException, IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got : " + sipServletResponse.toString());
		}
		if (sipServletResponse.getMethod().indexOf("BYE") != -1) {
			SipSession sipSession = sipServletResponse.getSession(false);
			if (sipSession != null && sipSession.isValid()) {
				sipSession.invalidate();
			}
			SipApplicationSession sipApplicationSession = sipServletResponse
					.getApplicationSession(false);
			if (sipApplicationSession != null
					&& sipApplicationSession.isValid()) {
				sipApplicationSession.invalidate();
			}
			return;
		}

		if (sipServletResponse.getMethod().indexOf("INVITE") != -1) {
			// if this is a response to an INVITE we ack it and forward the OK
			SipServletRequest ackRequest = sipServletResponse.createAck();
			if (logger.isInfoEnabled()) {
				logger.info("Sending " + ackRequest);
			}
			ackRequest.send();
			// create and sends OK for the first call leg
			SipServletRequest originalRequest = (SipServletRequest) sipServletResponse
					.getSession().getAttribute("originalRequest");
			SipServletResponse responseToOriginalRequest = originalRequest
					.createResponse(sipServletResponse.getStatus());
			if (logger.isInfoEnabled()) {
				logger.info("Sending OK on 1st call leg"
						+ responseToOriginalRequest);
			}
			responseToOriginalRequest.setContentLength(sipServletResponse
					.getContentLength());
			if (sipServletResponse.getContent() != null
					&& sipServletResponse.getContentType() != null)
				responseToOriginalRequest.setContent(
						sipServletResponse.getContent(),
						sipServletResponse.getContentType());
			responseToOriginalRequest.send();
		}
		if (sipServletResponse.getMethod().indexOf("UPDATE") != -1) {
			B2buaHelper helper = sipServletResponse.getRequest()
					.getB2buaHelper();
			SipServletRequest orgReq = helper
					.getLinkedSipServletRequest(sipServletResponse.getRequest());
			SipServletResponse res2 = orgReq.createResponse(sipServletResponse
					.getStatus());
			res2.send();
		}
	}

	@Override
	protected void doErrorResponse(SipServletResponse sipServletResponse)
			throws ServletException, IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got : " + sipServletResponse.getStatus() + " "
					+ sipServletResponse.getReasonPhrase());
		}
		// we don't forward the timeout
		if (sipServletResponse.getStatus() != 408) {
			// create and sends the error response for the first call leg
			SipServletRequest originalRequest = (SipServletRequest) sipServletResponse
					.getSession().getAttribute("originalRequest");
			SipServletResponse responseToOriginalRequest = originalRequest
					.createResponse(sipServletResponse.getStatus());
			if (logger.isInfoEnabled()) {
				logger.info("Sending on the first call leg "
						+ responseToOriginalRequest.toString());
			}
			responseToOriginalRequest.send();
		}
	}

	@Override
	protected void doProvisionalResponse(SipServletResponse sipServletResponse)
			throws ServletException, IOException {
		SipServletRequest originalRequest = (SipServletRequest) sipServletResponse
				.getSession().getAttribute("originalRequest");
		SipServletResponse responseToOriginalRequest = originalRequest
				.createResponse(sipServletResponse.getStatus());
		if (logger.isInfoEnabled()) {
			logger.info("Sending on the first call leg "
					+ responseToOriginalRequest.toString());
		}
		responseToOriginalRequest.send();
	}

	@Override
	public void doSubscribe(SipServletRequest req) throws ServletException,
			IOException {
		super.doSubscribe(req);
	}

	private void processRegister(SipServletRequest req) throws Exception {
		User user = null;
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
			String username = sipUri.getUser();
			user = userService.getUserByUsername(username);
			toURI = sipFactory.createSipURI(username, sipUri.getHost());
		} else {
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_UNSUPPORTED_URI_SCHEME);
			response.send();
			return;
		}
		if (user == null) {
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_NOT_FOUND);
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
