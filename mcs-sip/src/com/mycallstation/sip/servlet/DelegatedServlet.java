/**
 * 
 */
package com.mycallstation.sip.servlet;

import gov.nist.javax.sip.header.ims.PAssertedIdentityHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
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

import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.dataaccess.model.VoipVendor;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * @author Wei Gao
 * 
 */
@Configurable
@SipServlet(name = "DelegatedServlet", applicationName = "com.mycallstation.CallCenter", loadOnStartup = 1)
public class DelegatedServlet extends B2bServlet {
	private static final long serialVersionUID = 5263932525339104271L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mycallstation.sip.servlet.B2bServlet#doResponse(javax.servlet.sip.
	 * SipServletResponse)
	 */
	@Override
	protected void doResponse(SipServletResponse resp) throws ServletException,
			IOException {
		boolean needProcess = true;
		if (resp.getStatus() == SipServletResponse.SC_UNAUTHORIZED
				|| resp.getStatus() == SipServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED) {
			if (logger.isDebugEnabled()) {
				logger.debug("Get response: {}", resp);
			}
			needProcess = !processAuthInfo(resp);
		}
		if (needProcess) {
			super.doResponse(resp);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.servlet.B2bServlet#doInvite(javax.servlet.sip.
	 * SipServletRequest )
	 */
	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Processing to voip delegated invite.");
		}
		UserVoipAccount account = (UserVoipAccount) req
				.getAttribute(USER_VOIP_ACCOUNT);
		if (account == null) {
			if (logger.isErrorEnabled()) {
				logger.error("User voip account shouldn't been <null>.");
			}
			response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
		}

		String rmaddr = req.getInitialRemoteAddr();
		int rport = req.getInitialRemotePort();
		String rt = req.getInitialTransport();
		Address fa = req.getFrom();
		Address fb = (Address) fa.clone();
		Iterator<String> pns = fa.getParameterNames();
		while (pns.hasNext()) {
			fb.removeParameter(pns.next());
		}
		Address remoteEnd = sipFactory.createAddress(fb.getURI().clone());
		URI ruri = remoteEnd.getURI();
		if (ruri.isSipURI()) {
			final SipURI sruri = (SipURI) ruri;
			sruri.setHost(rmaddr);
			sruri.setTransportParam(rt);
			sruri.setPort(rport);
			remoteEnd.setURI(sruri);
		}
		URI remoteUri = sipUtil.getCanonicalizedURI(remoteEnd.getURI());

		req.getSession().setAttribute(REMOTE_URI, remoteUri);
		final SipURI toSipURI = (SipURI) req.getTo().getURI();
		VoipVendor vendor = account.getVoipVendor();
		Address toAddress = vendorManager.createToAddress(toSipURI.getUser(),
				account);
		Address fromAddress = vendorManager.createFromAddress(account);

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
		if (vendor.getProxy() != null) {
			Address routeAddress = sipFactory.createAddress("sip:"
					+ vendor.getProxy());
			forkedRequest.pushRoute(routeAddress);
		}
		forkedRequest.setRequestURI(toAddress.getURI());
		// Remove original authentication headers.
		forkedRequest.removeHeader(AuthorizationHeader.NAME);
		forkedRequest.removeHeader(PAssertedIdentityHeader.NAME);
		sipUtil.processingAddressInSDP(forkedRequest, req);
		if (logger.isTraceEnabled()) {
			logger.trace("Sending forked request: {}", forkedRequest);
		}
		forkedRequest.send();
		if (logger.isDebugEnabled()) {
			logger.debug(
					"{} is calling {} by {}.",
					new Object[] {
							account.getOwner().getDisplayName(),
							PhoneNumberUtil
									.getCanonicalizedPhoneNumber(toSipURI
											.getUser()),
							account.getVoipVendor().getName() });
		}
	}

	private boolean processAuthInfo(javax.servlet.sip.SipServletResponse resp)
			throws javax.servlet.ServletException, java.io.IOException {
		SipApplicationSession appSession = resp.getApplicationSession();
		UserVoipAccount account = null;
		B2buaHelper helper = resp.getRequest().getB2buaHelper();
		SipServletRequest origReq = helper.getLinkedSipServletRequest(resp
				.getRequest());
		if (origReq != null) {
			account = (UserVoipAccount) origReq.getAttribute(USER_VOIP_ACCOUNT);
		}
		if (account != null) {
			// Avoid re-sending if the auth repeatedly fails.
			if (!"true"
					.equals(appSession.getAttribute("FirstResponseRecieved"))) {
				if (logger.isTraceEnabled()) {
					logger.trace("First try.");
				}
				appSession.setAttribute("FirstResponseRecieved", "true");
				// Need to create request from current session but original
				// request. Otherwise, linked session in B2buaHelper will
				// be a mess.
				SipServletRequest challengeRequest = helper.createRequest(
						resp.getSession(), origReq, null);
				// Remove original authentication headers.
				challengeRequest.removeHeader(AuthorizationHeader.NAME);
				challengeRequest.removeHeader(PAssertedIdentityHeader.NAME);
				// Add new authentication headers
				challengeRequest.addAuthHeader(resp, account.getAccount(),
						account.getPassword());
				sipUtil.processingAddressInSDP(challengeRequest, origReq);
				if (logger.isTraceEnabled()) {
					logger.trace("Sending challenge request {}",
							challengeRequest);
				}
				challengeRequest.send();
				return true;
			}
		}
		return false;
	}
}
