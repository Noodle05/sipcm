/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSession.State;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.UAMode;
import javax.servlet.sip.URI;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.header.FromHeader;
import javax.sip.message.Request;

import com.mycallstation.constant.PhoneNumberStatus;
import com.mycallstation.dataaccess.model.AddressBinding;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.sip.events.CallEndEvent;
import com.mycallstation.sip.events.CallStartEvent;
import com.mycallstation.sip.locationservice.UserBindingInfo;
import com.mycallstation.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@SipServlet(name = "B2bServlet", applicationName = "com.mycallstation.CallCenter", loadOnStartup = 1)
public class B2bServlet extends AbstractSipServlet {
	private static final long serialVersionUID = -7798141358134636972L;

	public static final String LINKED_SESSION_STATUS = "com.mycallstation.linkedSessionStatus";
	public static final String REMOTE_URI = "com.mycallstation.remote.uri";

	@Resource(name = "sipUtil")
	protected SipUtil sipUtil;

	@Override
	protected void doRequest(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Get request: {}", req);
		}
		if (req.isInitial() || specialHandleRequest(req)) {
			if (logger.isTraceEnabled()) {
				logger.trace("This is a initial request or ACK or CANCEL, process with original logic.");
			}
			super.doRequest(req);
		} else {
			if ("INVITE".equals(req.getMethod())) {
				processReInvite(req);
				return;
			}
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
				if (resp.getStatus() == SipServletResponse.SC_REQUEST_TERMINATED) {
					logger.trace("487 already send on Cancel for intial leg UAS");
				}
			}
			SipSession session = resp.getSession(false);
			if (session != null && session.isValid()) {
				if (callEventListener != null) {
					callCanceled(session);
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Invalidate session: {}", session.getId());
				}
				session.invalidate();
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("No session on this 487 response.");
				}
			}
			return;
		}

		String cancelStatus = (String) resp.getSession().getAttribute(
				LINKED_SESSION_STATUS);
		if (cancelStatus != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Linked session cancelled already.");
			}
			if (cancelStatus.equals("WAITING")) {
				int status = resp.getStatus();
				if (status < 200) {
					if (logger.isTraceEnabled()) {
						logger.trace("Orginal request already send BYE to end this invite, so we send cancel");
					}
					SipServletRequest cancelRequest = resp.getRequest()
							.createCancel();
					cancelRequest.send();
					resp.getSession().setAttribute(LINKED_SESSION_STATUS,
							"TERMINATED");
				} else if (status < 300) {
					if (logger.isTraceEnabled()) {
						logger.trace("Orginal request already send BYE to end this invite, so we send bye");
					}
					SipServletRequest cancelRequest = resp.getSession()
							.createRequest(Request.BYE);
					cancelRequest.send();
					resp.getSession().setAttribute(LINKED_SESSION_STATUS,
							"TERMINATED");
				} else {
					if (logger.isTraceEnabled()) {
						logger.trace("Don't forward.");
					}
				}
			}
			return;
		}
		B2buaHelper helper = resp.getRequest().getB2buaHelper();
		SipSession session = resp.getSession();
		SipSession linkedSession = helper.getLinkedSession(session);
		if (callEventListener != null) {
			if ("INVITE".equalsIgnoreCase(resp.getRequest().getMethod())) {
				if (resp.getStatus() >= 200) {
					if (resp.getStatus() < 300) {
						callEstablished(session, linkedSession);
					} else {
						callFailed(session, linkedSession, resp);
					}
				}
			} else if ("BYE".equalsIgnoreCase(resp.getRequest().getMethod())) {
				callEnd(session, linkedSession);
			} else if ("CANCEL".equalsIgnoreCase(resp.getRequest().getMethod())) {
				callCanceled(session, linkedSession);
			}
		}
		SipServletResponse forkedResp = null;
		if (resp.getRequest().isInitial()) {
			forkedResp = helper.createResponseToOriginalRequest(linkedSession,
					resp.getStatus(), resp.getReasonPhrase());
		} else {
			SipServletRequest forkedReq = helper
					.getLinkedSipServletRequest(resp.getRequest());
			forkedResp = forkedReq.createResponse(resp.getStatus(),
					resp.getReasonPhrase());
		}
		copyContent(resp, forkedResp);
		if ("INVITE".equals(resp.getRequest().getMethod())
				&& (resp.getStatus() >= 200 && resp.getStatus() < 300)) {
			sipUtil.processingAddressInSDP(forkedResp, resp);
		}
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
		UserBindingInfo ubi = (UserBindingInfo) req
				.getAttribute(TARGET_USERSIPBINDING);
		if (ubi == null) {
			if (logger.isErrorEnabled()) {
				logger.error("Cannot found target user profile on request?");
			}
			response(req, SipServletResponse.SC_NOT_FOUND);
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Lookup result: ");
			for (AddressBinding b : ubi.getBindings()) {
				logger.trace("\t{}", b);
			}
		}
		AddressBinding binding = ubi.getBindings().iterator().next();
		if (logger.isTraceEnabled()) {
			logger.trace("Use address: \"{}\"", binding);
		}
		B2buaHelper helper = getB2buaHelper(req);
		UserSipProfile userSipProfile = (UserSipProfile) req
				.getAttribute(USER_ATTRIBUTE);
		SipServletRequest forkedRequest;
		if (userSipProfile != null) {
			String userName;
			if (userSipProfile.getPhoneNumber() == null
					|| PhoneNumberStatus.UNVERIFIED.equals(userSipProfile
							.getPhoneNumberStatus())) {
				userName = "";
			} else {
				userName = userSipProfile.getPhoneNumber();
			}
			SipURI fromUri = sipFactory.createSipURI(userName,
					appConfig.getDomain());
			Address fromAddr = sipFactory.createAddress(fromUri,
					userSipProfile.getDisplayName());
			Map<String, List<String>> headers = new HashMap<String, List<String>>();
			List<String> froms = new ArrayList<String>(1);
			froms.add(fromAddr.toString());
			headers.put(FromHeader.NAME, froms);
			forkedRequest = helper.createRequest(req, true, headers);
		} else {
			forkedRequest = helper.createRequest(req);
		}
		Address address = sipUtil.stringToSipAddress(binding.getAddress());
		Address remoteEnd = sipUtil.stringToSipAddress(binding.getRemoteEnd());
		URI remoteUri = sipUtil
				.getCanonicalizedURI(binding.getRemoteEnd() == null ? address
						.getURI() : remoteEnd.getURI());
		forkedRequest.getSession().setAttribute(REMOTE_URI, remoteUri);
		forkedRequest.setRequestURI(remoteUri);
		sipUtil.processingAddressInSDP(forkedRequest, req);
		if (logger.isTraceEnabled()) {
			logger.trace("Sending forked request {}", forkedRequest);
		}
		forkedRequest.send();
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
					(session == null ? null : session.getId()),
					(linkedSession == null ? null : linkedSession.getId()));
		}
		if (linkedSession != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Linked session {} state: {}",
						linkedSession.getId(), linkedSession.getState());
			}
			switch (linkedSession.getState()) {
			case INITIAL:
				if (logger.isTraceEnabled()) {
					logger.trace("Linked session still in INITIAL state, set waiting and will send cancel when receive 100-199 response.");
				}
				linkedSession.setAttribute(LINKED_SESSION_STATUS, "WAITING");
				if (callEventListener != null) {
					callCanceled(session);
				}
				response(req, SipServletResponse.SC_OK);
				break;
			case EARLY:
				if (logger.isTraceEnabled()) {
					logger.trace("Session still in early state, we should send cancel instead of bye.");
				}
				SipServletRequest cancel = helper.createCancel(linkedSession);
				if (logger.isDebugEnabled()) {
					logger.debug("Sending cancel to linked session. {}", cancel);
				}
				cancel.send();
				if (logger.isTraceEnabled()) {
					logger.trace("And response bad request response to original request.");
				}
				if (callEventListener != null) {
					callCanceled(session);
				}
				response(req, SipServletResponse.SC_BAD_REQUEST,
						"Use CANCEL next time.");
				if (logger.isTraceEnabled()) {
					logger.trace("Mark forked invite already cancelled.");
				}
				linkedSession.setAttribute(LINKED_SESSION_STATUS, "CANCELLED");
				break;
			case CONFIRMED:
				SipServletRequest forkedRequest = helper.createRequest(
						linkedSession, req, null);
				if (linkedSession.getAttribute(REMOTE_URI) != null) {
					forkedRequest.setRequestURI((URI) linkedSession
							.getAttribute(REMOTE_URI));
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Sending forked bye Request {}", forkedRequest);
				}
				forkedRequest.send();
				break;
			case TERMINATED:
				if (logger.isDebugEnabled()) {
					logger.debug("Linked session already terminated.");
				}
				response(req, SipServletResponse.SC_BAD_REQUEST,
						"Use CANCEL next time.");
				linkedSession.setAttribute(LINKED_SESSION_STATUS, "TERMINATED");
				break;
			}
			if (State.EARLY.equals(linkedSession.getState())) {
				return;
			}
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
		B2buaHelper helper = getB2buaHelper(req);
		SipSession origSession = helper.getLinkedSession(req.getSession());
		if (origSession != null) {
			List<SipServletMessage> msgs = helper.getPendingMessages(
					origSession, UAMode.UAC);
			for (SipServletMessage msg : msgs) {
				if (msg instanceof SipServletResponse) {
					SipServletResponse resp = (SipServletResponse) msg;
					if (resp.getStatus() == SipServletResponse.SC_OK) {
						SipServletRequest ack = resp.createAck();
						if (origSession.getAttribute(REMOTE_URI) != null) {
							ack.setRequestURI((URI) origSession
									.getAttribute(REMOTE_URI));
						}
						copyContent(req, ack);
						sipUtil.processingAddressInSDP(ack, req);
						if (logger.isTraceEnabled()) {
							logger.trace("Sending forked ACK: {}", ack);
						}
						ack.send();
					}
				}
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Cannot find original session.");
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

		if (linkedSession != null) {
			Object alreadyCancelled = linkedSession
					.getAttribute(LINKED_SESSION_STATUS);
			if (alreadyCancelled == null) {
				switch (linkedSession.getState()) {
				case INITIAL:
					if (logger.isTraceEnabled()) {
						logger.trace("Linked session still in INITIAL state, set waiting and will send cancel when receive 100-199 response.");
					}
					linkedSession
							.setAttribute(LINKED_SESSION_STATUS, "WAITING");
					if (callEventListener != null) {
						callCanceled(session);
					}
					break;
				case EARLY:
					SipServletRequest cancelRequest = helper
							.createCancel(linkedSession);
					if (logger.isDebugEnabled()) {
						logger.debug("Forwarding cancel request {}",
								cancelRequest);
					}
					if (linkedSession.getAttribute(REMOTE_URI) != null) {
						cancelRequest.setRequestURI((URI) linkedSession
								.getAttribute(REMOTE_URI));
					}
					cancelRequest.send();
					linkedSession.setAttribute(LINKED_SESSION_STATUS,
							"CANCELLED");
					if (callEventListener != null) {
						callCanceled(session);
					}
					break;
				case CONFIRMED:
					if (logger.isTraceEnabled()) {
						logger.trace("Linked session already confirmed, we need to send BYE.");
					}
					SipServletRequest bye = linkedSession
							.createRequest(Request.BYE);
					if (linkedSession.getAttribute(REMOTE_URI) != null) {
						bye.setRequestURI((URI) linkedSession
								.getAttribute(REMOTE_URI));
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Sending request {}", bye);
					}
					bye.send();
					break;
				case TERMINATED:
					if (logger.isTraceEnabled()) {
						logger.trace("Linked session already terminated.");
					}
					linkedSession.setAttribute(LINKED_SESSION_STATUS,
							"TERMINATED");
					break;
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Linked session {} already cancelled.",
							linkedSession.getId());
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"No linked session for this cancel request. original session id: {}",
						session.getId());
			}
		}
	}

	protected void processReInvite(SipServletRequest req)
			throws ServletException, IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("Simply forward re-invite to another leg.");
		}
		B2buaHelper helper = req.getB2buaHelper();
		SipSession linked = helper.getLinkedSession(req.getSession());
		SipServletRequest forkedReq = helper.createRequest(linked, req, null);
		copyContent(req, forkedReq);
		if ("INVITE".equals(req.getMethod())) {
			sipUtil.processingAddressInSDP(forkedReq, req);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Sending forked request: {}", forkedReq);
		}
		forkedReq.send();
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

	protected void callEnd(SipSession session, SipSession linkedSession) {
		callEnd(session);
		callEnd(linkedSession);
	}

	protected void callEnd(SipSession session) {
		if (session != null) {
			CallStartEvent startEvent = (CallStartEvent) session
					.getAttribute(INCOMING_CALL_START);
			if (startEvent != null) {
				session.removeAttribute(INCOMING_CALL_START);
				CallEndEvent endEvent = new CallEndEvent(startEvent);
				callEventListener.incomingCallEnd(endEvent);
			}
			startEvent = (CallStartEvent) session
					.getAttribute(OUTGOING_CALL_START);
			if (startEvent != null) {
				session.removeAttribute(OUTGOING_CALL_START);
				CallEndEvent endEvent = new CallEndEvent(startEvent);
				callEventListener.outgoingCallEnd(endEvent);
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("Calling \"{}\" with null session!", "callEnd");
			}
		}
	}

	protected void callCanceled(SipSession session, SipSession linkedSession) {
		callCanceled(session);
		callCanceled(linkedSession);

	}

	protected void callCanceled(SipSession session) {
		if (session != null) {
			CallStartEvent startEvent = (CallStartEvent) session
					.getAttribute(INCOMING_CALL_START);
			if (startEvent != null) {
				session.removeAttribute(INCOMING_CALL_START);
				CallEndEvent endEvent = new CallEndEvent(startEvent);
				callEventListener.incomingCallCancelled(endEvent);
			}
			startEvent = (CallStartEvent) session
					.getAttribute(OUTGOING_CALL_START);
			if (startEvent != null) {
				session.removeAttribute(OUTGOING_CALL_START);
				CallEndEvent endEvent = new CallEndEvent(startEvent);
				callEventListener.outgoingCallCancelled(endEvent);
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("Calling \"{}\" with null session!", "callCanceled");
			}
		}
	}

	protected void callEstablished(SipSession session, SipSession linkedSession) {
		callEstablished(session);
		callEstablished(linkedSession);
	}

	protected void callEstablished(SipSession session) {
		if (session != null) {
			CallStartEvent startEvent = (CallStartEvent) session
					.getAttribute(INCOMING_CALL_START);
			if (startEvent != null) {
				startEvent.setStartTime(new Date());
				callEventListener.incomingCallEstablished(startEvent);
			}
			startEvent = (CallStartEvent) session
					.getAttribute(OUTGOING_CALL_START);
			if (startEvent != null) {
				startEvent.setStartTime(new Date());
				callEventListener.outgoingCallEstablished(startEvent);
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("Calling \"{}\" with null session!",
						"callEstablished");
			}
		}
	}

	protected void callFailed(SipSession session, SipSession linkedSession,
			SipServletResponse resp) {
		callFailed(session, resp);
		callFailed(linkedSession, resp);
	}

	protected void callFailed(SipSession session, SipServletResponse resp) {
		if (session != null) {
			CallStartEvent startEvent = (CallStartEvent) session
					.getAttribute(INCOMING_CALL_START);
			if (startEvent != null) {
				session.removeAttribute(INCOMING_CALL_START);
				CallEndEvent endEvent = new CallEndEvent(startEvent,
						resp.getStatus(), resp.getReasonPhrase());
				callEventListener.incomingCallFailed(endEvent);
			}
			startEvent = (CallStartEvent) session
					.getAttribute(OUTGOING_CALL_START);
			if (startEvent != null) {
				session.removeAttribute(OUTGOING_CALL_START);
				CallEndEvent endEvent = new CallEndEvent(startEvent,
						resp.getStatus(), resp.getReasonPhrase());
				callEventListener.outgoingCallFailed(endEvent);
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("Calling \"{}\" with null session!", "callFailed");
			}
		}
	}
}