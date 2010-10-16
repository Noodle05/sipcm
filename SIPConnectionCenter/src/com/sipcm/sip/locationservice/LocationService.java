package com.sipcm.sip.locationservice;

import java.util.Collection;

import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;

import com.sipcm.common.model.User;

public interface LocationService {

	public void removeAllBinding(SipURI key) throws UserNotFoundException;

	public Binding getBinding(SipURI key, ContactHeader contactHeader);

	public void removeBinding(SipURI key, ContactHeader contactHeader)
			throws UserNotFoundException;

	public void updateRegistration(SipURI key, ContactHeader contactHeader,
			String callId, long cseq) throws UserNotFoundException;

	public Collection<ContactHeader> getContactHeaders(SipURI key)
			throws UserNotFoundException;

	public void register(SipURI key, User user, ContactHeader contactHeader,
			String callid, long cseq);
}