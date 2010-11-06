/**
 * 
 */
package com.sipcm.sip.servlet;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.annotation.SipServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sipcm.common.model.User;
import com.sipcm.sip.VoipVendorType;
import com.sipcm.sip.dialplan.DialplanExecutor;
import com.sipcm.sip.model.UserVoipAccount;
import com.sipcm.sip.util.MapHolderBean;

/**
 * @author wgao
 * 
 */
@Configurable
@SipServlet(name = "OutgoingPhoneInviteServlet", applicationName = "org.gaofamily.CallCenter", loadOnStartup = 1)
public class OutgoingPhoneInviteServlet extends AbstractSipServlet {
	private static final long serialVersionUID = 7063054667574623307L;

	@Autowired
	@Qualifier("dialplanExecutor")
	private DialplanExecutor dialplanExecutor;

	private Map<VoipVendorType, String> voipVendorToServletMap;

	@Autowired
	@Qualifier("mapHolderBean")
	private MapHolderBean mapHolderBean;

	@PostConstruct
	public void springInit() {
		voipVendorToServletMap = mapHolderBean.getVoipVendorToServletMap();
	}

	@Override
	protected void doInvite(SipServletRequest req) throws ServletException,
			IOException {
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
		String phoneNumber = (String) req.getAttribute(CALLING_PHONE_NUMBER);
		if (phoneNumber == null) {
			if (logger.isErrorEnabled()) {
				logger.error("No phone number on request?");
			}
			response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
			return;
		}
		User user = (User) req.getAttribute(USER_ATTRIBUTE);
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to excute dial plan.");
		}
		UserVoipAccount voipAccount = dialplanExecutor.execute(user,
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
		response(req, SipServletResponse.SC_SERVER_INTERNAL_ERROR);
	}
}
