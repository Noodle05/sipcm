/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.regex.Matcher;

import javax.annotation.PostConstruct;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.common.business.UserService;
import com.sipcm.common.model.User;
import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.dialplan.DialplanExecutor;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.util.MapHolderBean;
import com.sipcm.sip.util.ServerAuthenticationHelper;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "CallCenterServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class CallCenterServlet extends AbstractSipServlet {
	private static final long serialVersionUID = -4659953196279153841L;

	@Autowired
	@Qualifier("dialplanExecutor")
	private DialplanExecutor dialplanExecutor;

	private Map<VoipVendorType, String> voipVendorToServletMap;

	@Autowired
	@Qualifier("mapHolderBean")
	private MapHolderBean mapHolderBean;

	@Autowired
	@Qualifier("serverAuthenticationHelper")
	private ServerAuthenticationHelper authenticationHelper;

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@PostConstruct
	public void springInit() {
		voipVendorToServletMap = mapHolderBean.getVoipVendorToServletMap();
	}

	/**
	 * This will just forward request to RegistrarServlet
	 */
	@Override
	protected void doRegister(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get register request: {}", req);
		}
		User user = checkAuthentication(req);
		if (user == null) {
			return;
		}
		req.setAttribute(USER_ATTRIBUTE, user);
		RequestDispatcher dispatcher = req
				.getRequestDispatcher("RegistrarServlet");
		if (dispatcher != null) {
			dispatcher.forward(req, null);
		} else {
			response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR,
					"Cannot found registrar servlet.");
		}
	}

	/**
	 * Main logic on dispatch to different servlet.
	 */
	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get invite request: {}", req);
		}
		if (req.isInitial()) {
			if (logger.isTraceEnabled()) {
				logger.trace("This is initial request.");
			}
			URI toURI = req.getTo().getURI();
			URI fromURI = req.getFrom().getURI();
			if (!toURI.isSipURI() || !fromURI.isSipURI()) {
				if (logger.isDebugEnabled()) {
					logger.debug("We only recognize SIP request. Will response unsupported uri scheme.");
				}
				response(req, SipServletResponse.SC_UNSUPPORTED_URI_SCHEME);
				return;
			}
			final SipURI toSipUri = (SipURI) toURI;
			final SipURI fromSipUri = (SipURI) fromURI;
			String toHost = toSipUri.getHost();
			String fromHost = fromSipUri.getHost();
			if (!getDomain().equalsIgnoreCase(toHost)
					&& !getDomain().equalsIgnoreCase(fromHost)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Not from host nor to host is domain we served, response as forbidden.");
				}
				response(req, SipServletResponse.SC_FORBIDDEN,
						"Do not serve your domain.");
				return;
			}

			if (getDomain().equalsIgnoreCase(fromHost)) {
				if (logger.isTraceEnabled()) {
					logger.trace("From host is the domain we served, check authentication.");
				}
				User user = checkAuthentication(req);
				if (user == null) {
					if (logger.isTraceEnabled()) {
						logger.trace("Not passing authentication, response should already send, just return.");
					}
					return;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Authentication pass, set user to request.");
				}
				req.setAttribute(USER_ATTRIBUTE, user);
			}
			String toUser = toSipUri.getUser();
			if (getDomain().equalsIgnoreCase(toHost)) {
				Matcher m = PHONE_NUMBER.matcher(toUser);
				if (m.matches()) {
					if (logger.isTraceEnabled()) {
						logger.trace("This is a request to call a phone.");
					}
					if (req.getAttribute(USER_ATTRIBUTE) == null) {
						if (logger.isDebugEnabled()) {
							logger.debug("Only local user can call phone number. Response \"not acceptable\"");
						}
						// Only accept if it's from local user.
						response(req, SipServletResponse.SC_NOT_ACCEPTABLE);
						return;
					}
					User user = (User) req.getAttribute(USER_ATTRIBUTE);
					if (logger.isTraceEnabled()) {
						logger.trace("Trying to excute dial plan.");
					}
					UserVoipAccount voipAccount = dialplanExecutor.execute(
							user, phoneNumberUtil.getCanonicalizedPhoneNumber(m
									.group(1)));
					if (voipAccount != null) {
						if (logger.isDebugEnabled()) {
							logger.debug("Dialplan return {}", voipAccount);
						}
						req.setAttribute(USER_VOIP_ACCOUNT, voipAccount);
						String servlet = voipVendorToServletMap.get(voipAccount
								.getVoipVendor().getType());
						if (logger.isDebugEnabled()) {
							logger.debug("Forward to servlet: {}", servlet);
						}
						if (servlet != null) {
							RequestDispatcher dispatcher = req
									.getRequestDispatcher(servlet);
							if (dispatcher != null) {
								dispatcher.forward(req, null);
								return;
							} else {
								if (logger.isWarnEnabled()) {
									logger.warn("Cannot find request dispatcher based on servlet {}, response server internal error.");
								}
							}
						} else {
							if (logger.isWarnEnabled()) {
								logger.warn(
										"Cannot find servlet based on voip vendor type {}. Response server internal error",
										voipAccount.getVoipVendor().getType());
							}
						}
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("Dialplan excutor return <NULL>. Response server internal error.");
						}
					}
					response(req,
							SipServletResponse.SC_SERVER_INTERNAL_ERROR);
					return;
				} else {
					if (logger.isTraceEnabled()) {
						logger.trace("This is a incoming invite to local user.");
					}
					RequestDispatcher dispatcher = req
							.getRequestDispatcher("IncomingInviteServlet");
					if (dispatcher != null) {
						dispatcher.forward(req, null);
						return;
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("Cannot find incoming invite servlet, response server internal error.");
						}
						response(req,
								SipServletResponse.SC_SERVER_INTERNAL_ERROR);
						return;
					}
				}
			}

			if (logger.isTraceEnabled()) {
				logger.trace("Calling other voip user, forward to proxy servlet.");
			}

			RequestDispatcher dispatcher = req
					.getRequestDispatcher("ProxyServlet");
			if (dispatcher != null) {
				dispatcher.forward(req, null);
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Cannot find forward servlet, response server internal error.");
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
		} else {
			super.doInvite(req);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doOptions(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doOptions(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got options request: {}", req);
		}
		SipServletResponse response = req
				.createResponse(SipServletResponse.SC_OK);
		if (logger.isTraceEnabled()) {
			logger.trace("Sending response back: {}", response);
		}
		response.send();
	}

	private User checkAuthentication(SipServletRequest req) throws IOException {
		User user = null;
		if (authenticationHelper.authenticate(req)) {
			Principal principal = req.getUserPrincipal();
			String username = principal.getName();
			user = userService.getUserByUsername(username);
		}
		if (user == null) {
			response(req, SipServletResponse.SC_NOT_FOUND);
		}
		return user;
	}
}
