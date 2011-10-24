/**
 * 
 */
package com.mycallstation.sip.servlet;

import gov.nist.javax.sdp.fields.AttributeField;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;
import java.util.Vector;

import javax.annotation.Resource;
import javax.sdp.Attribute;
import javax.sdp.Connection;
import javax.sdp.MediaDescription;
import javax.sdp.Origin;
import javax.sdp.SdpConstants;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.sdp.SessionName;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.annotation.SipServlet;
import javax.sip.message.Response;

import com.mycallstation.dataaccess.business.UserSipProfileService;
import com.mycallstation.dataaccess.model.UserSipProfile;
import com.mycallstation.sip.keepalive.PhoneNumberKeepAlive;
import com.mycallstation.sip.nat.PublicIpAddressHolder;

/**
 * @author wgao
 * 
 */
@SipServlet(name = "KeepAliveServlet", applicationName = "com.mycallstation.CallCenter", loadOnStartup = 1)
public class KeepAliveServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 7066067294055190160L;

	public static final String SDP_TYPE = "application/sdp";
	public static final String KEEP_ALIVE_SESSION = "keepAliveSession";
	public static final String KEEP_ALIVE_USER = "keepAliveUser";
	public static final String KEEP_ALIVE_TIMER = "keepAliveTimer";

	@Resource(name = "publicIpAddressHolder")
	private PublicIpAddressHolder publicIpAddressHolder;

	@Resource(name = "userSipProfileService")
	private UserSipProfileService userSipProfileService;

	@Resource(name = "phoneNumberKeepAlive")
	private PhoneNumberKeepAlive keepAlive;

	private final Random random;
	private static final int minPort = 2048;
	private static final int maxPort = 65535;

	public KeepAliveServlet() {
		random = new Random();
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
		if (req.isInitial()) {
			UserSipProfile user = (UserSipProfile) req
					.getAttribute(USER_ATTRIBUTE);
			if (user != null) {
				try {
					SipApplicationSession appSession = req
							.getApplicationSession();
					appSession.setAttribute(KEEP_ALIVE_SESSION, true);
					appSession.setAttribute(KEEP_ALIVE_USER, user);
					SipServletResponse resp = req.createResponse(Response.OK);
					SessionDescription sdp;
					sdp = generateSessionDescription();
					String sdpStr = sdp.toString();
					resp.setContent(sdpStr, SDP_TYPE);
					if (logger.isTraceEnabled()) {
						logger.trace("Sending response: {}", resp);
					}
					resp.send();
				} catch (SdpException e) {
					if (logger.isWarnEnabled()) {
						logger.warn(
								"Error happened when generate SessionDescription",
								e);
					}
					response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
					keepAlive.removePingingUser(user);
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("Recevie ping call, but cannot find user?");
				}
				response(req, SipServletResponse.SC_NOT_FOUND);
			}
		} else {
			super.doInvite(req);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doAck(javax.servlet.sip.SipServletRequest)
	 */
	@Override
	protected void doAck(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("Get ACK.");
		}
		SipApplicationSession appSession = req.getApplicationSession();
		if (appSession.getAttribute(KEEP_ALIVE_SESSION) != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("This is a keep alive ping ACK. Creating hangup timer.");
			}
			timeService.createTimer(appSession,
					appConfig.getKeepAliveChatTime() * 1000L, true,
					new KeepAliveChatTimeoutProcessor());
			if (appSession.getAttribute(KEEP_ALIVE_USER) != null) {
				UserSipProfile user = (UserSipProfile) appSession
						.getAttribute(KEEP_ALIVE_USER);
				if (logger.isTraceEnabled()) {
					logger.trace("Remove user from pinging list.");
				}
				keepAlive.removePingingUser(user);
				if (logger.isTraceEnabled()) {
					logger.trace("Update user last received call.");
				}
				userSipProfileService.updateLastReceiveCallTime(user);
			}
		} else {
			super.doAck(req);
		}
	}

	private SessionDescription generateSessionDescription() throws SdpException {
		SdpFactory sdpFactory = SdpFactory.getInstance();
		SessionDescription sdp = sdpFactory.createSessionDescription();
		long sessionId = Math.abs(random.nextLong());
		Origin origin = sdp.getOrigin();
		origin.setUsername("root");
		origin.setSessionId(sessionId);
		origin.setSessionVersion(1L);
		InetAddress ip = publicIpAddressHolder.getPublicIp();
		if (ip != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Set ip to: ", ip.getHostAddress());
			}
			origin.setAddress(ip.getHostAddress());
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("No public IP");
			}
		}
		SessionName sn = sdp.getSessionName();
		sn.setValue("MyCallStation");
		Connection c = sdp.getConnection();
		if (c == null) {
			c = sdpFactory.createConnection(origin.getAddress());
			sdp.setConnection(c);
		} else {
			c.setAddress(origin.getAddress());
		}
		MediaDescription md = sdpFactory.createMediaDescription("audio",
				nextPort(), 1, SdpConstants.RTP_AVP, new int[] {
						SdpConstants.PCMU, SdpConstants.PCMA,
						SdpConstants.G729, 101 });
		Attribute rtpmap_0 = sdpFactory.createAttribute(SdpConstants.RTPMAP,
				"0 PCMU/8000");
		Attribute rtpmap_8 = sdpFactory.createAttribute(SdpConstants.RTPMAP,
				"8 PCMA/8000");
		Attribute rtpmap_18 = sdpFactory.createAttribute(SdpConstants.RTPMAP,
				"18 G729/8000");
		Attribute fmtp_18 = sdpFactory.createAttribute("fmtp", "18 annexb=no");
		Attribute rtpmap_101 = sdpFactory.createAttribute(SdpConstants.RTPMAP,
				"101 telephone-event/8000");
		Attribute fmtp_101 = sdpFactory.createAttribute("fmtp", "101 0-15");
		Attribute sendrecv = sdpFactory.createAttribute("sendrecv", null);
		md.addAttribute((AttributeField) rtpmap_0);
		md.addAttribute((AttributeField) rtpmap_8);
		md.addAttribute((AttributeField) rtpmap_18);
		md.addAttribute((AttributeField) fmtp_18);
		md.addAttribute((AttributeField) rtpmap_101);
		md.addAttribute((AttributeField) fmtp_101);
		md.addAttribute((AttributeField) sendrecv);

		Vector<MediaDescription> mds = new Vector<MediaDescription>();
		mds.add(md);
		sdp.setMediaDescriptions(mds);
		return sdp;
	}

	private int nextPort() {
		int ret = random.nextInt(maxPort - minPort) + minPort;
		ret += (ret % 2);
		return ret;
	}
}
