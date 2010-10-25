/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.locationservice.UserNotFoundException;
import com.sipcm.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@Configurable
public class InviteServlet extends AbstractSipServlet {
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

}
