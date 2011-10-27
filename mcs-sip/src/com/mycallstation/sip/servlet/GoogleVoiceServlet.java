/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.sdp.Attribute;
import javax.sdp.MediaDescription;
import javax.sdp.SdpConstants;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.header.ContactHeader;

import org.mobicents.servlet.sip.core.session.MobicentsSipSession;
import org.mobicents.servlet.sip.message.B2buaHelperImpl;
import org.mobicents.servlet.sip.message.SipServletRequestImpl;

import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.dataaccess.model.UserVoipAccount;
import com.mycallstation.googlevoice.GoogleVoiceManager;
import com.mycallstation.googlevoice.GoogleVoiceSession;
import com.mycallstation.sip.events.CallEndEvent;
import com.mycallstation.sip.events.CallStartEvent;
import com.mycallstation.sip.util.GvB2buaHelperImpl;
import com.mycallstation.util.PhoneNumberUtil;

/**
 * @author wgao
 * 
 */
@SipServlet(name = "GoogleVoiceServlet", applicationName = "com.mycallstation.CallCenter", loadOnStartup = 1)
public class GoogleVoiceServlet extends B2bServlet {
	private static final long serialVersionUID = 1812855574907498697L;

	public static final String ORIGINAL_SESSION = "com.mycallstation.original.session";
	public static final String GV_SESSION = "com.mycallstation.googlevoice.session";
	public static final String ORIGINAL_REQUEST = "com.mycallstation.originalRequest";
	public static final String GV_TIMEOUT = "com.mycallstation.googlevoice.timeout";
	public static final Pattern codecPattern = Pattern
			.compile("^(\\d+)\\s+.*$");
	public static final String SDP_TYPE = "application/sdp";

	@Resource(name = "googleVoiceManager")
	private GoogleVoiceManager googleVoiceManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mycallstation.sip.servlet.B2bServlet#doInvite(javax.servlet.sip.
	 * SipServletRequest )
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
			String appSessionIdKey = AbstractSipServlet
					.generateAppSessionKey(userSipProfile);
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
			SessionDescription[] sdps = null;
			try {
				sdps = findCommonCodec(origReq, req);
			} catch (Throwable e) {
				sdps = null;
			}
			SipServletResponse origResponse = origReq
					.createResponse(SipServletResponse.SC_OK);
			if (sdps != null) {
				setContent(origResponse, sdps[1]);
			} else {
				copyContent(req, origResponse);
			}
			sipUtil.processingAddressInSDP(origResponse, req);
			if (logger.isTraceEnabled()) {
				logger.trace("Sending OK to original request. {}", origResponse);
			}
			origResponse.send();
			// Response OK to this request.
			SipServletResponse response = req
					.createResponse(SipServletResponse.SC_OK);
			if (sdps != null) {
				setContent(response, sdps[0]);
			} else {
				copyContent(origReq, response);
			}
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
			String type = account.getCallBackType() == null ? "1" : account
					.getCallBackType().toString();
			if (gvSession.call(phoneNumber, type)) {
				if (logger.isDebugEnabled()) {
					logger.debug("{} is calling {} by google voice",
							userSipProfile.getDisplayName(), phoneNumber);
				}
				String appSessionIdKey = AbstractSipServlet
						.generateAppSessionKey(userSipProfile);
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
						appConfig.getGoogleVoiceCallTimeout() * 1000L, true,
						new GoogleVoiceTimeoutProcessor(req, userSipProfile));
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
	 * @see com.mycallstation.sip.servlet.B2bServlet#doCancel(javax.servlet.sip.
	 * SipServletRequest )
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
							callCanceled(req.getSession());
						}
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
	 * @see
	 * com.mycallstation.sip.servlet.B2bServlet#getB2buaHelper(javax.servlet
	 * .sip. SipServletRequest)
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

	private SessionDescription[] findCommonCodec(SipServletMessage msg1,
			SipServletMessage msg2) throws IOException, SdpException {
		SessionDescription sdp1 = getSdpFromMessage(msg1);
		SessionDescription sdp2 = getSdpFromMessage(msg2);
		if (sdp1 != null && sdp2 != null) {
			@SuppressWarnings("unchecked")
			Vector<MediaDescription> mds1 = sdp1.getMediaDescriptions(false);
			if (mds1 == null || mds1.isEmpty()) {
				return null;
			}
			@SuppressWarnings("unchecked")
			Vector<MediaDescription> mds2 = sdp2.getMediaDescriptions(false);
			if (mds2 == null || mds2.isEmpty()) {
				return null;
			}
			MediaDescription md1 = mds1.firstElement();
			MediaDescription md2 = mds2.firstElement();
			@SuppressWarnings("unchecked")
			Vector<String> codecs1 = md1.getMedia().getMediaFormats(false);
			if (codecs1 == null || codecs1.isEmpty()) {
				return null;
			}
			@SuppressWarnings("unchecked")
			Vector<String> codecs2 = md2.getMedia().getMediaFormats(false);
			if (codecs2 == null || codecs2.isEmpty()) {
				return null;
			}
			Integer codec = null;
			for (String c : codecs1) {
				if (Integer.parseInt(c) < SdpConstants.AVP_DEFINED_STATIC_MAX
						&& codecs2.contains(c)) {
					codec = Integer.parseInt(c);
					break;
				}
			}
			if (codec == null) {
				return null;
			}
			processMediaDescription(md1, codec);
			processMediaDescription(md2, codec);
			return new SessionDescription[] { sdp1, sdp2 };
		}
		return null;
	}

	private SessionDescription getSdpFromMessage(SipServletMessage msg)
			throws IOException, SdpParseException {
		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdp = null;
		if ("application/sdp".equals(msg.getContentType())
				&& msg.getContentLength() > 0) {
			byte[] t = msg.getRawContent();
			String enc = msg.getCharacterEncoding();
			if (enc == null) {
				enc = "UTF-8";
			}
			String c = new String(t, enc);
			sdp = sdpFactory.createSessionDescription(c);
		}
		return sdp;
	}

	private void setContent(SipServletMessage msg, SessionDescription sdp)
			throws UnsupportedEncodingException {
		String sdpStr = sdp.toString();
		msg.setContent(sdpStr, SDP_TYPE);
	}

	private void processMediaDescription(MediaDescription md, Integer codec)
			throws SdpException {
		@SuppressWarnings("unchecked")
		Vector<String> codecs = md.getMedia().getMediaFormats(false);
		Vector<String> oCodecs = new Vector<String>();
		oCodecs.add(codec.toString());
		for (String cstr : codecs) {
			int c = Integer.parseInt(cstr);
			if (c > SdpConstants.AVP_DEFINED_STATIC_MAX) {
				oCodecs.add(cstr);
			}
		}
		md.getMedia().setMediaFormats(oCodecs);
		@SuppressWarnings("unchecked")
		Vector<Attribute> attrs = md.getAttributes(false);
		Iterator<Attribute> itea = attrs.iterator();
		while (itea.hasNext()) {
			Attribute a = itea.next();
			if ("rtpmap".equals(a.getName()) || "fmtp".equals(a.getName())) {
				if (a.getValue() != null) {
					Matcher m = codecPattern.matcher(a.getValue());
					if (m.matches()) {
						String cstr = m.group(1);
						if (!oCodecs.contains(cstr)) {
							itea.remove();
						}
					}
				}
			}
		}
	}
}
