/**
 * 
 */
package com.sipcm.sip.proxy.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

import javax.annotation.Resource;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.springframework.stereotype.Component;

import com.sipcm.common.model.User;
import com.sipcm.sip.locationservice.Binding;
import com.sipcm.sip.locationservice.LocationService;
import com.sipcm.sip.util.SipUtil;

/**
 * @author wgao
 * 
 */
@Component("registerMethod")
public class RegisterMethod extends AbstractAuthenticatedMethod {
	public static final String SIP_MIN_EXPIRESTIME = "sip.expirestime.min";
	public static final String SIP_MAX_EXPIRESTIME = "sip.expirestime.max";

	@Resource(name = "sipUtil")
	private SipUtil sipUtil;

	@Resource(name = "sipLocationService")
	private LocationService locationService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sipcm.sip.proxy.plugins.AbstractAuthenticatedMethod#
	 * processAuthorizedIncomingRequest(javax.sip.message.Request,
	 * com.sipcm.common.model.User)
	 */
	@Override
	protected Response processAuthorizedIncomingRequest(Request request,
			User user) throws Exception {
		ToHeader toHeader = (ToHeader) request.getHeader(ToHeader.NAME);
		SipURI key = sipUtil.getLocationServiceKey(toHeader);
		@SuppressWarnings("unchecked")
		ListIterator<ContactHeader> ite = request
				.getHeaders(ContactHeader.NAME);
		Collection<ContactHeader> contactHeaders = new ArrayList<ContactHeader>();
		boolean wildChar = false;
		while (ite.hasNext()) {
			ContactHeader ch = ite.next();
			contactHeaders.add(ch);
			if (ch.isWildCard()) {
				wildChar = true;
			}
		}

		if (!contactHeaders.isEmpty()) {
			ExpiresHeader expiresHeader = (ExpiresHeader) request
					.getHeader(ExpiresHeader.NAME);
			if (wildChar) {
				if (contactHeaders.size() > 1 || expiresHeader == null
						|| expiresHeader.getExpires() != 0) {
					return messageFactory.createResponse(Response.BAD_REQUEST,
							request);
				}
				locationService.removeAllBinding(key);
			} else {
				int expiresTime = config.getInt(SIP_MAX_EXPIRESTIME);
				if (expiresHeader != null) {
					expiresTime = expiresHeader.getExpires();
				}
				expiresTime = correctExpiresTime(expiresTime);
				for (ContactHeader contactHeader : contactHeaders) {
					int contactExpiresTime = contactHeader.getExpires();

					if (contactExpiresTime == -1) {
						contactExpiresTime = expiresTime;
					}

					if (contactExpiresTime > 0) {
						contactExpiresTime = correctExpiresTime(contactExpiresTime);
					}
					contactHeader.setExpires(contactExpiresTime);

					Binding existingBinding;
					existingBinding = locationService.getBinding(key,
							contactHeader);
					String callId = ((CallIdHeader) request
							.getHeader(CallIdHeader.NAME)).getCallId();
					long cseq = ((CSeqHeader) request
							.getHeader(CSeqHeader.NAME)).getSeqNumber();
					if (existingBinding != null) {
						if (callId.equals(existingBinding.getCallId())
								&& cseq <= existingBinding.getCseq()) {
							return messageFactory.createResponse(
									Response.BAD_REQUEST, request);
						}
						if (contactHeader.getExpires() == 0) {
							locationService.removeBinding(key, contactHeader);
						} else {
							locationService.updateRegistration(key,
									contactHeader, callId, cseq);
						}
					} else {
						locationService.register(key, user, contactHeader,
								callId, cseq);
					}
				}
			}
		}
		contactHeaders = locationService.getContactHeaders(key);
		Response response = messageFactory.createResponse(Response.OK, request);
		for (ContactHeader contactHeader : contactHeaders) {
			response.addLast(contactHeader);
		}
		return response;
	}

	private int correctExpiresTime(int expiresTime) {
		if (expiresTime != 0) {
			expiresTime = Math.min(expiresTime,
					config.getInt(SIP_MAX_EXPIRESTIME, 3600));
			expiresTime = Math.max(expiresTime,
					config.getInt(SIP_MIN_EXPIRESTIME, 300));
		}
		return expiresTime;
	}
}
