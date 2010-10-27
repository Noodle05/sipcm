/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.locationservice.UserNotFoundException;
import com.sipcm.sip.locationservice.UserProfile;
import com.sipcm.sip.locationservice.UserProfileStatus;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "IncomingInviteServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class IncomingInviteServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 4938128598461987936L;

	@Autowired
	@Qualifier("sipLocationService")
	private LocationService locationService;

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
		RequestDispatcher dispatcher = null;
		SipURI toUri = (SipURI) req.getTo().getURI();
		SipURI fromUri = (SipURI) req.getFrom().getURI();
		String toUser = toUri.getUser();
		String fromUser = fromUri.getUser();
		if (PHONE_NUMBER.matcher(fromUser).matches()) {
			String key = sipFactory.createSipURI(toUser, toUri.getHost())
					.toString();
			UserProfile userProfile = null;
			try {
				userProfile = locationService.getUserProfile(key);
			} catch (UserNotFoundException e) {
				userProfile = null;
			}
			if (userProfile != null
					&& UserProfileStatus.WAITING_CALLBACK.equals(userProfile
							.getStatus())
					&& getCanonicalizedPhoneNumber(fromUser).equals(
							userProfile.getWaitingFromNumber())) {
				dispatcher = req.getRequestDispatcher("GoogleVoiceServlet");
			}
		}
		if (dispatcher == null) {
			dispatcher = req.getRequestDispatcher("B2bServlet");
		}
		dispatcher.forward(req, null);
	}
}
