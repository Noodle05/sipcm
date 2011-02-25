/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.annotation.SipServlet;

import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.dialplan.DialplanExecutor;
import com.sipcm.sip.events.CallStartEvent;
import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.locationservice.UserBindingInfo;
import com.sipcm.sip.model.AddressBinding;
import com.sipcm.sip.model.UserSipProfile;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.util.MapHolderBean;

/**
 * @author wgao
 * 
 */
@SipServlet(name = "OutgoingPhoneInviteServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class OutgoingPhoneInviteServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 7063054667574623307L;

	@Resource(name = "dialplanExecutor")
	private DialplanExecutor dialplanExecutor;

	@Resource(name = "sip.LocationService")
	private LocationService locationService;

	@Resource(name = "mapHolderBean")
	private MapHolderBean mapHolderBean;

	private Map<VoipVendorType, String> voipVendorToServletMap;

	@Override
	public void init() throws ServletException {
		super.init();
		// dialplanExecutor = (DialplanExecutor)
		// getServletContext().getAttribute(
		// "dialplanExecutor");
		// locationService = (LocationService) getServletContext().getAttribute(
		// "sip.LocationService");
		// mapHolderBean = (MapHolderBean) getServletContext().getAttribute(
		// "mapHolderBean");
		voipVendorToServletMap = mapHolderBean.getVoipVendorToServletMap();
	}

	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("This is a request to call a phone.");
		}
		UserSipProfile userSipProfile = (UserSipProfile) req
				.getAttribute(USER_ATTRIBUTE);
		if (userSipProfile == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Only local user can call phone number. Response \"not acceptable\"");
			}
			// Only accept if it's from local user.
			response(req, SipServletResponse.SC_NOT_ACCEPTABLE);
			return;
		}
		String phoneNumber = (String) req.getAttribute(CALLING_PHONE_NUMBER);
		if (phoneNumber == null) {
			if (logger.isErrorEnabled()) {
				logger.error("No phone number on request?");
			}
			response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
			return;
		}
		Collection<AddressBinding> addresses = locationService
				.getUserSipBindingByPhoneNumber(phoneNumber);
		if (addresses != null && !addresses.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Found local user with this phone number, will forward to local user directly.");
			}
			UserBindingInfo ubi = new UserBindingInfo(null, addresses);
			req.setAttribute(TARGET_USERSIPBINDING, ubi);
			RequestDispatcher dispather = req
					.getRequestDispatcher("IncomingInviteServlet");
			if (dispather == null) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot found target local invite servlet.");
				}
				response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
			} else {
				if (callEventListener != null) {
					CallStartEvent event = new CallStartEvent(userSipProfile,
							phoneNumber);
					req.getSession().setAttribute(OUTGOING_CALL_START, event);
					callEventListener.outgoingCallStart(event);
				}
				dispather.forward(req, null);
			}
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to excute dial plan.");
		}
		UserVoipAccount voipAccount = dialplanExecutor.execute(userSipProfile,
				phoneNumber);
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
					if (callEventListener != null) {
						CallStartEvent event = new CallStartEvent(voipAccount,
								phoneNumber);
						req.getSession().setAttribute(OUTGOING_CALL_START,
								event);
						callEventListener.outgoingCallStart(event);
					}
					dispatcher.forward(req, null);
					return;
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("Cannot find request dispatcher based on servlet {}, response server internal error.");
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
		response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
	}
}
