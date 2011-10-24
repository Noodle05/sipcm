/**
 * 
 */
package com.mycallstation.sip.servlet;

import java.io.IOException;

import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.MsControlFactory;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.SdpPortManager;
import javax.media.mscontrol.networkconnection.SdpPortManagerEvent;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.annotation.SipServlet;

/**
 * @author wgao
 * 
 */
@SipServlet(name = "GVVerificationServlet", applicationName = "com.mycallstation.CallCenter", loadOnStartup = 1)
public class GVVerificationSerlet extends AbstractSipServlet {
	private static final long serialVersionUID = -1855017140339513556L;

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
		if (isEnabled() && req.isInitial()) {
			SipServletResponse resp = req
					.createResponse(SipServletResponse.SC_RINGING);
			resp.send();
			try {
				MsControlFactory msControlFactory = (MsControlFactory) getServletContext()
						.getAttribute(
								MsControlFactoryInitializer.MS_CONTROL_FACTORY);
				MediaSession mediaSession = msControlFactory
						.createMediaSession();
				NetworkConnection conn = mediaSession
						.createNetworkConnection(NetworkConnection.BASIC);
				SdpPortManager sdpManager = conn.getSdpPortManager();
				MediaEventListener<SdpPortManagerEvent> listener = new NetworkConnectionListener();
				sdpManager.addListener(listener);
				byte[] sdpOffer = req.getRawContent();
				sdpManager.processSdpOffer(sdpOffer);
			} catch (MsControlException e) {
				if (logger.isErrorEnabled()) {
					logger.error("", e);
					req.createResponse(
							SipServletResponse.SC_SERVER_INTERNAL_ERROR).send();
				}
			}
		} else {
			super.doInvite(req);
		}
	}

	protected void terminate(SipSession sipSession, MediaSession mediaSession) {
		SipServletRequest bye = sipSession.createRequest("BYE");
		try {
			bye.send();
			// Clean up media session
			mediaSession.release();
			sipSession.removeAttribute("MEDIA_SESSION");
		} catch (Exception e1) {
			log("Terminating: Cannot send BYE: " + e1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.sip.SipServlet#doBye(javax.servlet.sip.SipServletRequest)
	 */
	@Override
	protected void doBye(SipServletRequest request) throws ServletException,
			IOException {

		logger.info("MediaPlaybackServlet: Got BYE request:\n" + request);
		MediaSession mediaSession = (MediaSession) request.getSession()
				.getAttribute("MEDIA_SESSION");
		mediaSession.release();

		SipServletResponse sipServletResponse = request
				.createResponse(SipServletResponse.SC_OK);
		sipServletResponse.send();
		// releasing the media connection

	}

	private boolean isEnabled() {
		return getServletContext().getAttribute(
				MsControlFactoryInitializer.MS_CONTROL_FACTORY) != null;
	}

	private class NetworkConnectionListener implements
			MediaEventListener<SdpPortManagerEvent> {

		public void onEvent(SdpPortManagerEvent event) {

			SdpPortManager sdpmana = event.getSource();
			NetworkConnection conn = sdpmana.getContainer();
			MediaSession mediaSession = event.getSource().getMediaSession();

			SipSession sipSession = (SipSession) mediaSession
					.getAttribute("SIP_SESSION");

			SipServletRequest inv = (SipServletRequest) sipSession
					.getAttribute("UNANSWERED_INVITE");
			sipSession.removeAttribute("UNANSWERED_INVITE");

			if (event.isSuccessful()) {
				SipServletResponse resp = inv
						.createResponse(SipServletResponse.SC_OK);
				try {
					byte[] sdp = event.getMediaServerSdp();

					resp.setContent(sdp, "application/sdp");
					// Send 200 OK
					resp.send();
					if (logger.isDebugEnabled()) {
						logger.debug("Sent OK Response for INVITE");
					}

					sipSession.setAttribute("NETWORK_CONNECTION", conn);

				} catch (Exception e) {
					logger.error("", e);

					// Clean up
					sipSession.getApplicationSession().invalidate();
					mediaSession.release();
				}
			} else {
				try {
					if (SdpPortManagerEvent.SDP_NOT_ACCEPTABLE.equals(event
							.getError())) {

						if (logger.isDebugEnabled()) {
							logger.debug("Sending SipServletResponse.SC_NOT_ACCEPTABLE_HERE for INVITE");
						}
						// Send 488 error response to INVITE
						inv.createResponse(
								SipServletResponse.SC_NOT_ACCEPTABLE_HERE)
								.send();
					} else if (SdpPortManagerEvent.RESOURCE_UNAVAILABLE
							.equals(event.getError())) {
						if (logger.isDebugEnabled()) {
							logger.debug("Sending SipServletResponse.SC_BUSY_HERE for INVITE");
						}
						// Send 486 error response to INVITE
						inv.createResponse(SipServletResponse.SC_BUSY_HERE)
								.send();
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("Sending SipServletResponse.SC_SERVER_INTERNAL_ERROR for INVITE");
						}
						// Some unknown error. Send 500 error response to INVITE
						inv.createResponse(
								SipServletResponse.SC_SERVER_INTERNAL_ERROR)
								.send();
					}
					// Clean up media session
					sipSession.removeAttribute("MEDIA_SESSION");
					mediaSession.release();
				} catch (Exception e) {
					logger.error("", e);

					// Clean up
					sipSession.getApplicationSession().invalidate();
					mediaSession.release();
				}
			}
		}

	}
}
