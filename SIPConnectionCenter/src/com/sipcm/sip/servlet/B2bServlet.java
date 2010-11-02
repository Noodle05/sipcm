/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.UAMode;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.message.Request;

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

	private static final String[] specialHandleRequest = new String[] {
			Request.ACK, Request.CANCEL, Request.BYE };

	static {
		Arrays.sort(specialHandleRequest);
	}

	@Override
	protected void doRequest(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get request: {}", req);
		}
		if (req.isInitial()
				|| (Arrays.binarySearch(specialHandleRequest, req.getMethod()) >= 0)) {
			if (logger.isTraceEnabled()) {
				logger.trace("This is a initial request or ACK or CANCEL, process with original logic.");
			}
			super.doRequest(req);
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Simply forward to another leg.");
			}
			B2buaHelper helper = req.getB2buaHelper();
			SipSession linked = helper.getLinkedSession(req.getSession());
			SipServletRequest forkedReq = helper.createRequest(linked, req,
					null);
			copyContent(req, forkedReq);
			if (logger.isTraceEnabled()) {
				logger.trace("Sending forked request: {}", forkedReq);
			}
			forkedReq.send();
		}
	}

	@Override
	protected void doResponse(SipServletResponse resp) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get response: {}", resp);
		}
		if (resp.getStatus() == SipServletResponse.SC_REQUEST_TERMINATED) {
			if (logger.isTraceEnabled()) {
				logger.trace("487 already send on Cancel for intial leg UAS");
			}
			SipSession session = resp.getSession(false);
			if (session != null && session.isValid()) {
				session.invalidate();
			}
			return;
		}

		B2buaHelper helper = resp.getRequest().getB2buaHelper();
		SipSession linked = helper.getLinkedSession(resp.getSession());
		SipServletResponse forkedResp = null;
		if (resp.getRequest().isInitial()) {
			forkedResp = helper.createResponseToOriginalRequest(linked,
					resp.getStatus(), resp.getReasonPhrase());
		} else {
			SipServletRequest forkedReq = helper
					.getLinkedSipServletRequest(resp.getRequest());
			forkedResp = forkedReq.createResponse(resp.getStatus(),
					resp.getReasonPhrase());
		}
		copyContent(resp, forkedResp);
		if (logger.isTraceEnabled()) {
			logger.trace("Sending forked response: {}", forkedResp);
		}
		forkedResp.send();
	}

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
			response(req, SipServletResponse.SC_NOT_FOUND);
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
			// forkedRequest.getSession().setAttribute(ORIGINAL_REQUEST, req);
			if (logger.isTraceEnabled()) {
				logger.trace("Sending forked request {}", forkedRequest);
			}
			forkedRequest.send();
		} else {
			response(req, SipServletResponse.SC_NOT_FOUND);
			return;
		}
	}

	/**
	 * bye is end to end request, shouldn't response first.
	 */
	@Override
	protected void doBye(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got BYE: " + req.toString());
		}

		// we forward the BYE
		SipSession session = req.getSession();
		B2buaHelper helper = getB2buaHelper(req);
		SipSession linkedSession = helper.getLinkedSession(session);
		if (logger.isDebugEnabled()) {
			logger.debug("This request session id: {}, linked session id: {}",
					session.getId(), linkedSession.getId());
		}
		if (linkedSession != null) {
			SipServletRequest forkedRequest = helper.createRequest(
					linkedSession, req, null);
			if (logger.isTraceEnabled()) {
				logger.trace("Sending forked bye Request {}", forkedRequest);
			}
			forkedRequest.send();
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot found linked session, or linked session is not in confirm stage, will not send forked request.");
			}
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_NOT_FOUND);
			if (logger.isTraceEnabled()) {
				logger.trace("Sendind not found back to bye request: {}",
						response);
			}
			response.send();
		}
		return;
	}

	@Override
	protected void doAck(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get ACK: {}", req);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Getting all pending message (UAC mode)");
		}
		B2buaHelper help = req.getB2buaHelper();
		SipSession origSession = help.getLinkedSession(req.getSession());
		List<SipServletMessage> msgs = help.getPendingMessages(origSession,
				UAMode.UAC);
		for (SipServletMessage msg : msgs) {
			if (msg instanceof SipServletResponse) {
				SipServletResponse resp = (SipServletResponse) msg;
				if (resp.getStatus() == SipServletResponse.SC_OK) {
					SipServletRequest ack = resp.createAck();
					copyContent(req, ack);
					if (logger.isTraceEnabled()) {
						logger.trace("Sending forked ACK: ", ack);
					}
					ack.send();
				}
			}
		}
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

		SipServletRequest cancelRequest = helper.createCancel(linkedSession);
		if (logger.isDebugEnabled()) {
			logger.debug("Forwarding cancel request {}", cancelRequest);
		}
		cancelRequest.send();
	}

	protected B2buaHelper getB2buaHelper(SipServletRequest req) {
		return req.getB2buaHelper();
	}

	protected void copyContent(SipServletMessage source, SipServletMessage dest)
			throws UnsupportedEncodingException, IOException {
		if (source.getContentLength() > 0) {
			dest.setContentLength(source.getContentLength());
			dest.setContent(source.getContent(), source.getContentType());
			String enc = source.getCharacterEncoding();
			if (enc != null && enc.length() > 0) {
				dest.setCharacterEncoding(enc);
			}
		}
	}
}
