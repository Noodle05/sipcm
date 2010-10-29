/**
 * 
 */
package com.sipcm.sip.servlet;

import gov.nist.javax.sip.header.ims.PAssertedIdentityHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.AuthInfo;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;

import org.springframework.beans.factory.annotation.Configurable;

import com.sipcm.sip.model.UserVoipAccount;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "DelegatedServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class DelegatedServlet extends B2bServlet {
	private static final long serialVersionUID = 5263932525339104271L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.servlet.B2bServlet#processInitialInvite(javax.servlet.sip
	 * .SipServletRequest)
	 */
	@Override
	protected void processInitialInvite(SipServletRequest req)
			throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Processing to voip delegated invite.");
		}
		SipApplicationSession appSession = req.getApplicationSession();
		UserVoipAccount account = (UserVoipAccount) appSession
				.getAttribute(USER_VOIP_ACCOUNT);
		if (account == null) {
			if (logger.isErrorEnabled()) {
				logger.error("User voip account shouldn't been <null>.");
			}
			responseError(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
		}
		final SipURI toSipURI = (SipURI) req.getTo().getURI();
		URI toURI = sipFactory.createSipURI(toSipURI.getUser(), account
				.getVoipVendor().getDomain());
		SipURI fromURI = sipFactory.createSipURI(account.getAccount(), account
				.getVoipVendor().getDomain());
		Address toAddress = sipFactory.createAddress(toURI);
		Address fromAddress = sipFactory.createAddress(fromURI, req.getFrom()
				.getDisplayName());

		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		List<String> address = new ArrayList<String>(1);
		address.add(fromAddress.toString());
		headers.put(FromHeader.NAME, address);

		address = new ArrayList<String>(1);
		address.add(toAddress.toString());
		headers.put(ToHeader.NAME, address);

		B2buaHelper helper = getB2buaHelper(req);
		SipServletRequest forkedRequest = helper.createRequest(req, true,
				headers);
		forkedRequest.setRequestURI(toURI);
		// Remove original authentication headers.
		forkedRequest.removeHeader(AuthorizationHeader.NAME);
		forkedRequest.removeHeader(PAssertedIdentityHeader.NAME);
		forkedRequest.getSession().setAttribute(ORIGINAL_REQUEST, req);
		if (logger.isTraceEnabled()) {
			logger.trace("Sending forked request: {}", forkedRequest);
		}
		forkedRequest.send();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.servlet.B2bServlet#doErrorResponse(javax.servlet.sip.
	 * SipServletResponse)
	 */
	@Override
	protected void doErrorResponse(javax.servlet.sip.SipServletResponse resp)
			throws javax.servlet.ServletException, java.io.IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got error response {}", resp);
		}
		if (resp.getStatus() == SipServletResponse.SC_UNAUTHORIZED
				|| resp.getStatus() == SipServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED) {
			SipApplicationSession appSession = resp.getApplicationSession();
			UserVoipAccount account = (UserVoipAccount) appSession
					.getAttribute(USER_VOIP_ACCOUNT);
			if (account != null) {
				// Avoid re-sending if the auth repeatedly fails.
				if (!"true".equals(appSession
						.getAttribute("FirstResponseRecieved"))) {
					if (logger.isTraceEnabled()) {
						logger.trace("First try.");
					}
					appSession.setAttribute("FirstResponseRecieved", "true");
					B2buaHelper helper = resp.getRequest().getB2buaHelper();
					// Need to create request from current session but original
					// request. Otherwise, linked session in B2buaHelper will
					// be a mess.
					SipServletRequest origRequest = (SipServletRequest) resp
							.getSession().getAttribute(ORIGINAL_REQUEST);
					AuthInfo authInfo = sipFactory.createAuthInfo();
					authInfo.addAuthInfo(resp.getStatus(), account
							.getVoipVendor().getDomain(), account.getAccount(),
							account.getPassword());
					SipServletRequest challengeRequest = helper.createRequest(
							resp.getSession(), origRequest, null);
					// Remove original authentication headers.
					challengeRequest.removeHeader(AuthorizationHeader.NAME);
					challengeRequest.removeHeader(PAssertedIdentityHeader.NAME);
					// Add new authentication headers
					challengeRequest.addAuthHeader(resp, authInfo);
					if (logger.isTraceEnabled()) {
						logger.trace("Sending challenge request {}",
								challengeRequest);
					}
					challengeRequest.send();
					return;
				}
			}
		}
		if (resp.getStatus() != SipServletResponse.SC_REQUEST_TIMEOUT) {
			// create and sends the error response for the first call leg
			SipServletRequest originalRequest = (SipServletRequest) resp
					.getSession().getAttribute(ORIGINAL_REQUEST);
			SipServletResponse responseToOriginalRequest = originalRequest
					.createResponse(resp.getStatus());
			if (logger.isTraceEnabled()) {
				logger.trace("Sending on the first call leg ",
						responseToOriginalRequest);
			}
			responseToOriginalRequest.send();
		}

	}
}
