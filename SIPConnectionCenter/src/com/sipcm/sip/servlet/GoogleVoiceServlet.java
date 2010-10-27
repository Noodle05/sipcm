/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.googlevoice.GoogleVoiceManager;
import com.sipcm.googlevoice.GoogleVoiceSession;
import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.locationservice.UserNotFoundException;
import com.sipcm.sip.locationservice.UserProfile;
import com.sipcm.sip.locationservice.UserProfileStatus;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "GoogleVoiceServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class GoogleVoiceServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 1812855574907498697L;
	public static final Pattern USA_CANADA_PHONE = Pattern
			.compile("^([\\d{7}|\\d{10}|1\\d{10}])$");

	public static final Pattern PHONE_NUMBER = Pattern.compile("^(\\+?\\d+)$");

	@Autowired
	@Qualifier("sipLocationService")
	private LocationService locationService;

	@Autowired
	@Qualifier("sipUtil")
	private SipUtil sipUtil;

	@Autowired
	@Qualifier("googleVoiceManager")
	private GoogleVoiceManager googleVoiceManager;

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
		if (req.isInitial()) {
			URI toURI = req.getTo().getURI();
			URI fromURI = req.getFrom().getURI();
			if (!toURI.isSipURI() || !fromURI.isSipURI()) {
				SipServletResponse response = req
						.createResponse(SipServletResponse.SC_UNSUPPORTED_URI_SCHEME);
				response.send();
				return;
			}
			final SipURI toSipUri = (SipURI) toURI;
			final SipURI fromSipUri = (SipURI) fromURI;
			String toHost = toSipUri.getHost();
			String fromHost = fromSipUri.getHost();
			if (!getDomain().equalsIgnoreCase(toHost)
					&& !getDomain().equalsIgnoreCase(fromHost)) {
				SipServletResponse response = req.createResponse(
						SipServletResponse.SC_FORBIDDEN,
						"Do not serve your domain.");
				response.send();
				return;
			}

			String toUser = toSipUri.getUser();
			Matcher m = PHONE_NUMBER.matcher(toUser);
			if (m.matches()) {
				if (!getDomain().equalsIgnoreCase(fromHost)) {
					SipServletResponse response = req.createResponse(
							SipServletResponse.SC_FORBIDDEN,
							"Do not serve your domain.");
					response.send();
					return;
				}
				processOutgoingPhoneInvite(req, m.group(1));
				return;
			}
			processOutgoingB2bInvite(req);
			return;
		} else {
			processForwardingInvite(req);
			return;
		}
	}

	private void processGoogleVoiceCallBack(SipServletRequest req,
			UserProfile userProfile) {
		System.out.println("This is for debug purpose.");
	}

	private void processOutgoingPhoneInvite(SipServletRequest req,
			String phoneNumber) throws ServletException, IOException {
		final SipURI fromSipUri = (SipURI) req.getFrom().getURI();
		URI fromUri = sipFactory.createSipURI(fromSipUri.getUser(),
				fromSipUri.getHost());
		UserProfile userProfile = null;
		try {
			userProfile = locationService.getUserProfile(fromUri.toString());
		} catch (UserNotFoundException e) {
			SipServletResponse response = req.createResponse(
					SipServletResponse.SC_PRECONDITION_FAILURE,
					"In order to call phone number, you need register first.");
			response.send();
			return;
		}
		Set<UserVoipAccount> voipAccounts = userProfile.getUser()
				.getVoipAccounts();
		UserVoipAccount gvAccount = null;
		if (voipAccounts != null) {
			for (UserVoipAccount uva : voipAccounts) {
				if (VoipVendorType.GOOGLE_VOICE.equals(uva.getVoipVendor()
						.getType())) {
					gvAccount = uva;
					break;
				}
			}
		}
		if (gvAccount == null) {
			SipServletResponse response = req.createResponse(
					SipServletResponse.SC_PRECONDITION_FAILURE,
					"I support google voice only.");
			response.send();
			return;
		}
		GoogleVoiceSession gvSession = googleVoiceManager
				.getGoogleVoiceSession(gvAccount.getAccount(),
						gvAccount.getPassword(), gvAccount.getPhoneNumber());
		try {
			gvSession.login();
			gvSession.call(phoneNumber, "1");
			userProfile.setStatus(UserProfileStatus.WAITING_CALLBACK);
			userProfile.setGvSession(gvSession);
		} catch (Exception e) {
			SipServletResponse response = req.createResponse(
					SipServletResponse.SC_BAD_GATEWAY,
					"Cannot make google voice call.");
			response.send();
			return;
		}
	}

	private void processOutgoingB2bInvite(SipServletRequest req)
			throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Processing to voip outgoing invite.");
		}
		final SipURI toSipURI = (SipURI) req.getTo().getURI();
		URI toURI = sipFactory.createSipURI(toSipURI.getUser(),
				toSipURI.getHost());
		UserProfile userProfile = null;
		try {
			if (logger.isTraceEnabled()) {
				logger.trace("Lookup address with key: {}", toURI);
			}
			userProfile = locationService.getUserProfile(toURI.toString());
		} catch (UserNotFoundException e) {
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_NOT_FOUND);
			response.send();
			return;
		}
		if (UserProfileStatus.WAITING_CALLBACK.equals(userProfile.getStatus())) {
			GoogleVoiceSession gvSession = userProfile.getGvSession();
			if (toSipURI.getUser().equals(
					getCanonicalizedPhoneNumber(gvSession.getMyNumber()))) {
				processGoogleVoiceCallBack(req, userProfile);
				return;
			}
		}
		Collection<Address> addresses = null;
		addresses = userProfile.getAddresses();
		if (logger.isTraceEnabled()) {
			logger.trace("Lookup result: ");
			for (Address a : addresses) {
				logger.trace("\t{}", a);
			}
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
	}

	private void processForwardingInvite(SipServletRequest req)
			throws ServletException, IOException {
		B2buaHelper helper = req.getB2buaHelper();
		SipSession peerSession = helper.getLinkedSession(req.getSession());
		SipServletRequest invite = helper.createRequest(peerSession, req, null);
		invite.getSession().setAttribute("originalRequest", req);
		invite.send();
	}

	@Override
	protected void doBye(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got BYE: " + req.toString());
		}
		// we send the OK directly to the first call leg
		SipServletResponse sipServletResponse = req
				.createResponse(SipServletResponse.SC_OK);
		sipServletResponse.send();

		// we forward the BYE
		SipSession session = req.getSession();
		B2buaHelper helper = req.getB2buaHelper();
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
	protected void doUpdate(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got UPDATE: " + req.toString());
		}
		B2buaHelper helper = req.getB2buaHelper();
		SipSession peerSession = helper.getLinkedSession(req.getSession());
		SipServletRequest update = helper.createRequest(peerSession, req, null);
		update.send();
	}

	@Override
	protected void doCancel(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got CANCEL: " + req.toString());
		}
		SipSession session = req.getSession();
		B2buaHelper helper = req.getB2buaHelper();
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
	protected void doSuccessResponse(SipServletResponse response)
			throws ServletException, IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got : " + response.toString());
		}
		if (response.getMethod().indexOf("BYE") != -1) {
			SipSession sipSession = response.getSession(false);
			if (sipSession != null && sipSession.isValid()) {
				sipSession.invalidate();
			}
			SipApplicationSession sipApplicationSession = response
					.getApplicationSession(false);
			if (sipApplicationSession != null
					&& sipApplicationSession.isValid()) {
				sipApplicationSession.invalidate();
			}
			return;
		}

		if (response.getMethod().indexOf("INVITE") != -1) {
			// if this is a response to an INVITE we ack it and forward the OK
			SipServletRequest ackRequest = response.createAck();
			if (logger.isInfoEnabled()) {
				logger.info("Sending " + ackRequest);
			}
			ackRequest.send();
			// create and sends OK for the first call leg
			SipServletRequest originalRequest = (SipServletRequest) response
					.getSession().getAttribute("originalRequest");
			SipServletResponse responseToOriginalRequest = originalRequest
					.createResponse(response.getStatus());
			if (logger.isInfoEnabled()) {
				logger.info("Sending OK on 1st call leg"
						+ responseToOriginalRequest);
			}
			responseToOriginalRequest.setContentLength(response
					.getContentLength());
			if (response.getContent() != null
					&& response.getContentType() != null)
				responseToOriginalRequest.setContent(response.getContent(),
						response.getContentType());
			responseToOriginalRequest.send();
		}
		if (response.getMethod().indexOf("UPDATE") != -1) {
			B2buaHelper helper = response.getRequest().getB2buaHelper();
			SipServletRequest orgReq = helper
					.getLinkedSipServletRequest(response.getRequest());
			SipServletResponse res2 = orgReq.createResponse(response
					.getStatus());
			res2.send();
		}
	}

	@Override
	protected void doErrorResponse(SipServletResponse response)
			throws ServletException, IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Got : " + response.getStatus() + " "
					+ response.getReasonPhrase());
		}
		// we don't forward the timeout
		if (response.getStatus() != 408) {
			// create and sends the error response for the first call leg
			SipServletRequest originalRequest = (SipServletRequest) response
					.getSession().getAttribute("originalRequest");
			SipServletResponse responseToOriginalRequest = originalRequest
					.createResponse(response.getStatus());
			if (logger.isInfoEnabled()) {
				logger.info("Sending on the first call leg "
						+ responseToOriginalRequest.toString());
			}
			responseToOriginalRequest.send();
		}
	}

	@Override
	protected void doProvisionalResponse(SipServletResponse response)
			throws ServletException, IOException {
		SipServletRequest originalRequest = (SipServletRequest) response
				.getSession().getAttribute("originalRequest");
		SipServletResponse responseToOriginalRequest = originalRequest
				.createResponse(response.getStatus());
		if (logger.isInfoEnabled()) {
			logger.info("Sending on the first call leg "
					+ responseToOriginalRequest.toString());
		}
		responseToOriginalRequest.send();
	}
}
