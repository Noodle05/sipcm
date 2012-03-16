package com.mycallstation.sip.util;

import gov.nist.javax.sip.header.ims.PAssertedIdentityHeader;

import java.security.Principal;

import javax.annotation.Resource;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.sip.address.SipURI;
import javax.sip.address.TelURL;

import org.apache.catalina.Realm;
import org.mobicents.servlet.sip.SipFactories;
import org.mobicents.servlet.sip.core.session.MobicentsSipApplicationSession;
import org.mobicents.servlet.sip.message.SipServletRequestImpl;
import org.mobicents.servlet.sip.message.SipServletResponseImpl;
import org.mobicents.servlet.sip.security.SipSecurityUtils;
import org.mobicents.servlet.sip.security.authentication.DigestAuthenticator;
import org.mobicents.servlet.sip.startup.SipContext;
import org.mobicents.servlet.sip.startup.loading.SipLoginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("serverAuthenticationHelper")
public class ServerAuthenticationHelper {
	private static final Logger logger = LoggerFactory
			.getLogger(ServerAuthenticationHelper.class);

	@Resource(name = "sipLoginConfig")
	private SipLoginConfig loginConfig;

	public boolean authenticate(SipServletRequest req) {
		MobicentsSipApplicationSession appSession = (MobicentsSipApplicationSession) req
				.getApplicationSession();
		SipContext sipStandardContext = appSession.getSipContext();
		SipServletRequestImpl request = (SipServletRequestImpl) req;
		boolean authenticated = false;
		try {
			String authMethod = loginConfig.getAuthMethod();
			if (authMethod != null) {
				String pAssertedIdentitySetting = loginConfig
						.getIdetitySchemeSettings(SipLoginConfig.IDENTITY_SCHEME_P_ASSERTED);
				if (pAssertedIdentitySetting != null) {
					if (request.getHeader(PAssertedIdentityHeader.NAME) != null) {
						String pAssertedHeaderValue = request
								.getHeader(PAssertedIdentityHeader.NAME);

						// If P-Identity is required we must send error message
						// immediately
						if (pAssertedHeaderValue == null
								&& SipLoginConfig.IDENTITY_SCHEME_REQUIRED
										.equals(pAssertedIdentitySetting)) {
							request.createResponse(
									SipServletResponse.SC_USE_IDENTITY_HEADER,
									"P-Asserted-Idetity header is required!")
									.send();
							return false;
						}
						javax.sip.address.Address address = SipFactories.addressFactory
								.createAddress(pAssertedHeaderValue);
						String username = null;
						if (address.getURI().isSipURI()) {
							SipURI sipUri = (SipURI) address.getURI();
							username = sipUri.getUser();
						} else {
							TelURL telUri = (TelURL) address.getURI();
							username = telUri.getPhoneNumber();
						}
						Realm realm = sipStandardContext.getRealm();
						Principal principal = SipSecurityUtils
								.impersonatePrincipal(username, realm);

						if (principal != null) {
							authenticated = true;
							request.setUserPrincipal(principal);
							request.getSipSession().setUserPrincipal(principal);
							if (logger.isDebugEnabled()) {
								logger.debug("P-Asserted-Identity authetication successful for user: "
										+ username);
							}
						}
					}
				}
				// (2) Then if P-Identity has failed and is not required attempt
				// DIGEST
				// auth
				if (!authenticated && authMethod.equalsIgnoreCase("DIGEST")) {
					DigestAuthenticator digestAuthenticator = new DigestAuthenticator();
					digestAuthenticator.setContext(sipStandardContext);
					SipServletResponseImpl response = createErrorResponse(request);
					authenticated = digestAuthenticator.authenticate(request,
							response, loginConfig);
					request.setUserPrincipal(digestAuthenticator.getPrincipal());
				} else if (authMethod.equalsIgnoreCase("BASIC")) {
					throw new IllegalStateException(
							"Basic authentication not supported in JSR 289");
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("No login configuration found in sip.xml. We won't authenticate.");
				}
				return true; // There is no auth config in sip.xml. So don't
								// authenticate.
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error happened when authentcation request.", e);
			}
		}
		return authenticated;
	}

	private SipServletResponseImpl createErrorResponse(
			SipServletRequestImpl request) {
		SipServletResponse response = null;
		response = request.createResponse(SipServletResponse.SC_UNAUTHORIZED);
		return (SipServletResponseImpl) response;
	}

}
