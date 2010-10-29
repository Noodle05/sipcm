/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Collection;

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

import org.mobicents.servlet.sip.core.RoutingState;
import org.mobicents.servlet.sip.message.SipServletRequestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.locationservice.UserNotFoundException;
import com.sipcm.sip.locationservice.UserProfile;
import com.sipcm.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "B2bServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class B2bServlet extends AbstractSipServlet {
	private static final long serialVersionUID = -7798141358134636972L;

	@Autowired
	@Qualifier("sipLocationService")
	private LocationService locationService;

	@Autowired
	@Qualifier("sipUtil")
	private SipUtil sipUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doInvite(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get invite request: {}", req);
		}
		if (req.isInitial()) {
			processInitialInvite(req);
			return;
		} else {
			processInDialogInvite(req);
			return;
		}
	}

	protected void processInitialInvite(SipServletRequest req)
			throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Processing to voip back to back invite.");
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
			responseError(req, SipServletResponse.SC_NOT_FOUND);
			return;
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
			B2buaHelper helper = getB2buaHelper(req);
			SipServletRequest forkedRequest = helper.createRequest(req);
			forkedRequest.setRequestURI(sipUtil.getCanonicalizedURI(address
					.getURI()));
			forkedRequest.getSession().setAttribute(ORIGINAL_REQUEST, req);
			if (logger.isTraceEnabled()) {
				logger.trace("Sending forked request {}", forkedRequest);
			}
			forkedRequest.send();
		} else {
			responseError(req, SipServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	protected void processInDialogInvite(SipServletRequest req)
			throws ServletException, IOException {
		B2buaHelper helper = getB2buaHelper(req);
		SipSession peerSession = helper.getLinkedSession(req.getSession());
		SipServletRequest invite = helper.createRequest(peerSession, req, null);
		invite.getSession().setAttribute(ORIGINAL_REQUEST, req);
		if (logger.isTraceEnabled()) {
			logger.trace("forwarding in dialog invite request {}", invite);
		}
		invite.send();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doBye(javax.servlet.sip.SipServletRequest)
	 */
	@Override
	protected void doBye(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got BYE: " + req.toString());
		}
		// we send the OK directly to the first call leg
		SipServletResponse sipServletResponse = req
				.createResponse(SipServletResponse.SC_OK);
		if (logger.isTraceEnabled()) {
			logger.trace("Response ok to original request.");
		}
		sipServletResponse.send();

		// we forward the BYE
		SipSession session = req.getSession();
		B2buaHelper helper = getB2buaHelper(req);
		SipSession linkedSession = helper.getLinkedSession(session);
		if (linkedSession != null) {
			SipServletRequest forkedRequest = linkedSession
					.createRequest("BYE");
			if (logger.isTraceEnabled()) {
				logger.trace("Sending forked bye Request {}", forkedRequest);
			}
			forkedRequest.send();
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Cannot found linked session, got bye at same time?");
			}
		}
		if (session != null && session.isValid()) {
			session.invalidate();
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doUpdate(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doUpdate(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got UPDATE: " + req.toString());
		}
		B2buaHelper helper = getB2buaHelper(req);
		SipSession peerSession = helper.getLinkedSession(req.getSession());
		if (peerSession == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("Cannot find linked session!");
			}
		}
		SipServletRequest update = helper.createRequest(peerSession, req, null);
		if (logger.isTraceEnabled()) {
			logger.trace("Forwarding update request {}", update);
		}
		update.send();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doCancel(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doCancel(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got CANCEL: {}", req.toString());
		}
		SipSession session = req.getSession();
		B2buaHelper helper = getB2buaHelper(req);
		SipSession linkedSession = helper.getLinkedSession(session);
		SipServletRequest originalRequest = (SipServletRequest) linkedSession
				.getAttribute(ORIGINAL_REQUEST);
		SipServletRequest cancelRequest = helper.getLinkedSipServletRequest(
				originalRequest).createCancel();
		if (logger.isDebugEnabled()) {
			logger.debug("Forwarding cancel request {}", cancelRequest);
		}
		cancelRequest.send();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.sip.SipServlet#doSuccessResponse(javax.servlet.sip.
	 * SipServletResponse)
	 */
	@Override
	protected void doSuccessResponse(SipServletResponse response)
			throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got : " + response.toString());
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
			if (logger.isDebugEnabled()) {
				logger.debug("Sending " + ackRequest);
			}
			ackRequest.send();
			// create and sends OK for the first call leg
			SipServletRequest originalRequest = (SipServletRequest) response
					.getSession().getAttribute(ORIGINAL_REQUEST);
			SipServletResponse responseToOriginalRequest = originalRequest
					.createResponse(response.getStatus());
			responseToOriginalRequest.setContentLength(response
					.getContentLength());
			if (response.getContent() != null
					&& response.getContentType() != null)
				responseToOriginalRequest.setContent(response.getContent(),
						response.getContentType());
			if (logger.isTraceEnabled()) {
				logger.trace("Sending OK on 1st call leg {}",
						responseToOriginalRequest);
			}
			responseToOriginalRequest.send();
		}
		if (response.getMethod().indexOf("UPDATE") != -1) {
			B2buaHelper helper = getB2buaHelper(response.getRequest());
			SipServletRequest orgReq = helper
					.getLinkedSipServletRequest(response.getRequest());
			SipServletResponse res2 = orgReq.createResponse(response
					.getStatus());
			res2.send();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.sip.SipServlet#doErrorResponse(javax.servlet.sip.
	 * SipServletResponse)
	 */
	@Override
	protected void doErrorResponse(SipServletResponse response)
			throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got : " + response.getStatus() + " "
					+ response.getReasonPhrase());
		}
		// we don't forward the timeout
		if (response.getStatus() != SipServletResponse.SC_REQUEST_TIMEOUT) {
			// create and sends the error response for the first call leg
			SipServletRequest originalRequest = (SipServletRequest) response
					.getSession().getAttribute(ORIGINAL_REQUEST);
			if (originalRequest instanceof SipServletRequestImpl) {
				final SipServletRequestImpl req = (SipServletRequestImpl) originalRequest;
				if (RoutingState.CANCELLED.equals(req.getRoutingState())) {
					// The original request should be google voice invite
					// request, already canceled.
					if (logger.isTraceEnabled()) {
						logger.trace(
								"Original invite request canceled already, maybe a google voice request. Original invite: {}",
								req);
					}
					return;
				}
			}
			SipServletResponse responseToOriginalRequest = originalRequest
					.createResponse(response.getStatus());
			if (logger.isTraceEnabled()) {
				logger.trace("Sending on the first call leg ",
						responseToOriginalRequest);
			}
			responseToOriginalRequest.send();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doProvisionalResponse(javax.servlet.sip.
	 * SipServletResponse)
	 */
	@Override
	protected void doProvisionalResponse(SipServletResponse response)
			throws ServletException, IOException {
		SipServletRequest originalRequest = (SipServletRequest) response
				.getSession().getAttribute(ORIGINAL_REQUEST);
		SipServletResponse responseToOriginalRequest = originalRequest
				.createResponse(response.getStatus());
		if (logger.isDebugEnabled()) {
			logger.debug("Sending on the first call leg "
					+ responseToOriginalRequest.toString());
		}
		responseToOriginalRequest.send();
	}

	protected B2buaHelper getB2buaHelper(SipServletRequest req) {
		return req.getB2buaHelper();
	}
}
