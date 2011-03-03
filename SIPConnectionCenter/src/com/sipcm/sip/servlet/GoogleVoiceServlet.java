/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipListener;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.header.ContactHeader;

import org.mobicents.servlet.sip.core.session.MobicentsSipSession;
import org.mobicents.servlet.sip.message.B2buaHelperImpl;
import org.mobicents.servlet.sip.message.SipServletRequestImpl;

import com.sipcm.googlevoice.GoogleVoiceManager;
import com.sipcm.googlevoice.GoogleVoiceSession;
import com.sipcm.sip.events.CallEndEvent;
import com.sipcm.sip.events.CallStartEvent;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.util.GvB2buaHelperImpl;
import com.sipcm.sip.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
@SipServlet(name = "GoogleVoiceServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
@SipListener
public class GoogleVoiceServlet extends B2bServlet implements TimerListener {
	private static final long serialVersionUID = 1812855574907498697L;

	public static final String ORIGINAL_SESSION = "com.sipcm.original.session";
	public static final String GV_SESSION = "com.sipcm.googlevoice.session";
	public static final String ORIGINAL_REQUEST = "com.sipcm.originalRequest";
	public static final String GV_TIMEOUT = "com.sipcm.googlevoice.timeout";

	@Resource(name = "googleVoiceManager")
	private GoogleVoiceManager googleVoiceManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.servlet.B2bServlet#doInvite(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		SipURI toUri = (SipURI) req.getTo().getURI();
		String toUser = toUri.getUser();
		if (req.getAttribute(CALLING_PHONE_NUMBER) != null) {
			String phoneNumber = (String) req
					.getAttribute(CALLING_PHONE_NUMBER);
			// This is initial call
			SipApplicationSession appSession = req.getApplicationSession();
			Principal p = req.getUserPrincipal();
			if (p == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot found login prinipal for outgoing call? this should never happen.");
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			// String username = p.getName();
			UserSipProfile userSipProfile = (UserSipProfile) req
					.getAttribute(USER_ATTRIBUTE);
			UserVoipAccount account = (UserVoipAccount) req
					.getAttribute(USER_VOIP_ACCOUNT);
			if (userSipProfile == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot found user from request? This should never happen.");
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			if (account == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot found voip account for {}? This should never happen");
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			processGoogleVoiceCall(req, appSession, userSipProfile, account,
					phoneNumber);
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Google voice call back request");
			}
			// This is call back.
			UserSipProfile userSipProfile = (UserSipProfile) req
					.getAttribute(USER_ATTRIBUTE);
			String appSessionIdKey = generateAppSessionKey(userSipProfile);
			String appSessionId = (String) getServletContext().getAttribute(
					appSessionIdKey);
			if (appSessionId == null) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Cannot found appSessionId for {}? This should never happen",
							appSessionIdKey);
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			SipApplicationSession appSession = sipSessionsUtil
					.getApplicationSessionById(appSessionId);
			if (appSession == null) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"Cannot found application session for {}? This should never happen",
							toUser);
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				return;
			}
			getServletContext().removeAttribute(appSessionIdKey);
			if (logger.isTraceEnabled()) {
				logger.trace("Removing google voice callback number and google voice session from applciation.");
			}
			appSession.removeAttribute(GV_WAITING_FOR_CALLBACK);
			GoogleVoiceSession gvSession = (GoogleVoiceSession) appSession
					.getAttribute(GV_SESSION);
			appSession.removeAttribute(GV_SESSION);
			if (gvSession != null) {
				gvSession.logout();
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Removing google voice timeout timer.");
			}
			String timerId = (String) appSession.getAttribute(GV_TIMEOUT);
			if (timerId != null) {
				appSession.removeAttribute(GV_TIMEOUT);
				ServletTimer timer = appSession.getTimer(timerId);
				if (timer != null) {
					timer.cancel();
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Response OK to both side.");
			}
			SipSession session = req.getSession();
			SipSession origSession = (SipSession) appSession
					.getAttribute(ORIGINAL_SESSION);
			B2buaHelper helper = getB2buaHelper(req);
			helper.linkSipSessions(session, origSession);
			SipServletRequest origReq = (SipServletRequest) origSession
					.getAttribute(ORIGINAL_REQUEST);
			// Response OK to original request first.
			SipServletResponse origResponse = origReq
					.createResponse(SipServletResponse.SC_OK);
			copyContent(req, origResponse);
			sipUtil.processingAddressInSDP(origResponse, req);
			if (logger.isTraceEnabled()) {
				logger.trace("Sending OK to original request. {}", origResponse);
			}
			origResponse.send();
			// Response OK to this request.
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_OK);
			copyContent(origReq, response);
			sipUtil.processingAddressInSDP(response, origReq);
			if (logger.isTraceEnabled()) {
				logger.trace("Sending OK to callback request. {}", response);
			}
			response.send();
			if (callEventListener != null) {
				callEstablished(origSession, session);
			}
		}
	}

	private void processGoogleVoiceCall(SipServletRequest req,
			SipApplicationSession appSession, UserSipProfile userSipProfile,
			UserVoipAccount account, String phoneNumber) throws IOException {
		GoogleVoiceSession gvSession = googleVoiceManager
				.getGoogleVoiceSession(account.getAccount(),
						account.getPassword(), account.getCallBackNumber());
		appSession.setAttribute(GV_SESSION, gvSession);
		try {
			gvSession.login();
			if (gvSession.call(phoneNumber, "1")) {
				if (logger.isDebugEnabled()) {
					logger.debug("{} is calling {} by google voice",
							userSipProfile.getDisplayName(), phoneNumber);
				}
				String appSessionIdKey = generateAppSessionKey(userSipProfile);
				appSession.setAttribute(
						GV_WAITING_FOR_CALLBACK,
						PhoneNumberUtil.getCanonicalizedPhoneNumber(
								account.getPhoneNumber(),
								userSipProfile.getDefaultAreaCode()));
				getServletContext().setAttribute(appSessionIdKey,
						appSession.getId());
				SipSession session = req.getSession();
				session.setAttribute(APPLICATION_SESSION_ID, appSessionIdKey);
				session.setAttribute(ORIGINAL_REQUEST, req);
				appSession.setAttribute(ORIGINAL_SESSION, session);
				ServletTimer st = timeService.createTimer(appSession,
						appConfig.getGoogleVoiceCallTimeout() * 1000L, false,
						(SipServletRequestImpl) req);
				appSession.setAttribute(GV_TIMEOUT, st.getId());
				response(req, SipServletResponse.SC_SESSION_PROGRESS,
						"Waiting for callback.");
				URI remoteEnd = getRemoteEndURI(req);
				if (remoteEnd != null) {
					req.getSession().setAttribute(REMOTE_URI, remoteEnd);
				}
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("Google voice call failed.");
				}
				response(req, SipServletResponse.SC_DECLINE,
						"Google voice call failed.");
				callFailed(req.getSession(), "Google voice call failed.");
				gvSession.logout();
				return;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info(
						"Error happened during google voice call. Error message: {}",
						e.getMessage());
				if (logger.isDebugEnabled()) {
					logger.debug("Exception stack: ", e);
				}
			}
			response(req, SipServletResponse.SC_BAD_GATEWAY);
			callFailed(req.getSession(), e);
			gvSession.logout();
			return;
		}
	}

	private URI getRemoteEndURI(SipServletRequest req) {
		try {
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
				if (wildChar) {
					if (logger.isWarnEnabled()) {
						logger.warn("INVITE with contact \"*\"?");
					}
					return null;
				}
				String rmaddr = req.getInitialRemoteAddr();
				int rport = req.getInitialRemotePort();
				String rt = req.getInitialTransport();

				Address a = contacts.iterator().next();
				Address remoteEnd = sipFactory
						.createAddress(a.getURI().clone());
				URI ruri = remoteEnd.getURI();
				if (ruri.isSipURI()) {
					final SipURI sruri = (SipURI) ruri;
					sruri.setHost(rmaddr);
					sruri.setTransportParam(rt);
					sruri.setPort(rport);
					remoteEnd.setURI(sruri);
				}
				return remoteEnd.getURI();
			}
		} catch (ServletParseException e) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error happened when parsing contact header. Turn on debug to see detail exception stack.");
				if (logger.isDebugEnabled()) {
					logger.debug("Detail exception stack:", e);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sipcm.sip.servlet.B2bServlet#doCancel(javax.servlet.sip.SipServletRequest
	 * )
	 */
	@Override
	protected void doCancel(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Got CANCEL: ", req);
		}
		URI fromUri = req.getFrom().getURI();
		if (fromUri.isSipURI()) {
			String appSessionIdKey = (String) req.getSession().getAttribute(
					APPLICATION_SESSION_ID);
			if (appSessionIdKey != null) {
				String appSessionId = (String) getServletContext()
						.getAttribute(appSessionIdKey);
				if (appSessionId != null) {
					getServletContext().removeAttribute(appSessionIdKey);
					SipApplicationSession appSession = sipSessionsUtil
							.getApplicationSessionById(appSessionId);
					if (appSession != null) {
						String timerId = (String) appSession
								.getAttribute(GV_TIMEOUT);
						if (timerId != null) {
							appSession.removeAttribute(GV_TIMEOUT);
							if (logger.isTraceEnabled()) {
								logger.trace(
										"Get google voice timeout timer id: {}",
										timerId);
							}
							ServletTimer timer = appSession.getTimer(timerId);
							if (timer != null) {
								if (logger.isTraceEnabled()) {
									logger.trace("Found google voice timeout timer, cancel it.");
								}
								timer.cancel();
							} else {
								if (logger.isDebugEnabled()) {
									logger.debug(
											"Cannot find timer by timer id: {}",
											timerId);
								}
							}
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("Cannot find timer id on application session.");
							}
						}
						GoogleVoiceSession gvSession = (GoogleVoiceSession) appSession
								.getAttribute(GV_SESSION);
						if (gvSession != null) {
							try {
								if (gvSession.cancel()) {
									if (logger.isDebugEnabled()) {
										logger.debug("Google voice call had been canceld");
									}
								} else {
									if (logger.isInfoEnabled()) {
										logger.info("Error happened when cancel google voice call.");
									}
								}
							} catch (Exception e) {
								if (logger.isWarnEnabled()) {
									logger.warn("Unable to cancel google voice call.");
								}
							} finally {
								gvSession.logout();
							}
						}
						if (callEventListener != null) {
							callCancelled(req.getSession());
						}
						appSession.invalidate();
						return;
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("Cannot found appSession by id: {}",
									appSessionId);
						}
					}
				}
			}
		}
		super.doCancel(req);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.servlet.B2bServlet#getB2buaHelper(javax.servlet.sip.
	 * SipServletRequest)
	 */
	@Override
	protected B2buaHelper getB2buaHelper(SipServletRequest req) {
		if (req instanceof SipServletRequestImpl) {
			final SipServletRequestImpl request = (SipServletRequestImpl) req;
			final MobicentsSipSession session = request.getSipSession();
			if (session.getProxy() != null)
				throw new IllegalStateException("Proxy already present");
			B2buaHelperImpl b2buaHelper = session.getB2buaHelper();
			if (b2buaHelper != null && b2buaHelper instanceof GvB2buaHelperImpl)
				return b2buaHelper;
			if (b2buaHelper == null) {
				b2buaHelper = (B2buaHelperImpl) req.getB2buaHelper();
			}
			GvB2buaHelperImpl helper = new GvB2buaHelperImpl(b2buaHelper);
			session.setB2buaHelper(helper);
			return helper;
		} else {
			return req.getB2buaHelper();
		}
	}

	@Override
	public void timeout(ServletTimer timer) {
		if (logger.isDebugEnabled()) {
			logger.debug("Timeout, google voice didn't go though in time, cancel it.");
		}
		SipServletRequest req = (SipServletRequest) timer.getInfo();
		if (req == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("Google voice timeout, but request object is null?");
			}
			return;
		}
		UserSipProfile userSipProfile = (UserSipProfile) req
				.getAttribute(USER_ATTRIBUTE);
		String appSessionIdKey = generateAppSessionKey(userSipProfile);
		getServletContext().removeAttribute(appSessionIdKey);
		SipApplicationSession appSession = timer.getApplicationSession();
		GoogleVoiceSession gvSession = (GoogleVoiceSession) appSession
				.getAttribute(GV_SESSION);
		if (gvSession != null) {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Cancelling google voice call.");
				}
				gvSession.cancel();
				if (logger.isTraceEnabled()) {
					logger.trace("Google voice call canceled.");
				}
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Error happened when cancel google voice call.", e);
				}
			} finally {
				gvSession.logout();
			}
		}
		if (State.INITIAL.equals(req.getSession().getState())
				|| State.EARLY.equals(req.getSession().getState())) {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Response timeout to original INVITE request.");
				}
				response(req, SipServletResponse.SC_REQUEST_TIMEOUT);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Error happened when sendig timeout response to original request.");
				}
			}
		}
	}

	protected void callFailed(SipSession session, Exception e) {
		CallStartEvent startEvent = (CallStartEvent) session
				.getAttribute(INCOMING_CALL_START);
		if (startEvent != null) {
			session.removeAttribute(INCOMING_CALL_START);
			CallEndEvent endEvent = new CallEndEvent(startEvent,
					SipServletResponse.SC_BAD_GATEWAY, e.getMessage());
			callEventListener.incomingCallFailed(endEvent);
		}
		startEvent = (CallStartEvent) session.getAttribute(OUTGOING_CALL_START);
		if (startEvent != null) {
			session.removeAttribute(OUTGOING_CALL_START);
			CallEndEvent endEvent = new CallEndEvent(startEvent,
					SipServletResponse.SC_BAD_GATEWAY, e.getMessage());
			callEventListener.outgoingCallFailed(endEvent);
		}
	}

	protected void callFailed(SipSession session, String msg) {
		CallStartEvent startEvent = (CallStartEvent) session
				.getAttribute(INCOMING_CALL_START);
		if (startEvent != null) {
			session.removeAttribute(INCOMING_CALL_START);
			CallEndEvent endEvent = new CallEndEvent(startEvent,
					SipServletResponse.SC_BAD_GATEWAY, msg);
			callEventListener.incomingCallFailed(endEvent);
		}
		startEvent = (CallStartEvent) session.getAttribute(OUTGOING_CALL_START);
		if (startEvent != null) {
			session.removeAttribute(OUTGOING_CALL_START);
			CallEndEvent endEvent = new CallEndEvent(startEvent,
					SipServletResponse.SC_BAD_GATEWAY, msg);
			callEventListener.outgoingCallFailed(endEvent);
		}
	}
}
