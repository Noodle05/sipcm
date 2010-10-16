/**
 * 
 */
package com.sipcm.sip.proxy.plugins;

import javax.annotation.Resource;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wgao
 * 
 */
public abstract class AbstractMethod implements Method {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "applicationConfiguration")
	protected Configuration config;

	@Resource(name = "sipAddressFactory")
	protected AddressFactory addressFactory;

	@Resource(name = "sipHeaderFactory")
	protected HeaderFactory headerFactory;

	@Resource(name = "sipMessageFactory")
	protected MessageFactory messageFactory;

	public static final String DOMAIN_NAME = "domainname";

	public Response processRequest(Request request) throws Exception {
		Response response = preProcessRequest(request);
		try {
			if (response == null) {
				response = processIncomingRequest(request);
				response = postProcessRequest(request, response);
			}
		} finally {
			try {
				postProcessRequestFinal(request);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Post process request final error happened. Will just ignore it.",
							e);
				}
			}
		}
		return response;
	}

	protected abstract Response processIncomingRequest(Request request)
			throws Exception;

	protected String getDomain() {
		return config.getString(DOMAIN_NAME);
	}

	protected Response preProcessRequest(Request request) throws Exception {
		URI uri = request.getRequestURI();
		if (!uri.isSipURI()) {
			return messageFactory.createResponse(
					Response.UNSUPPORTED_URI_SCHEME, request);
		}
		final SipURI sipUri = (SipURI) uri;
		String host = sipUri.getHost();
		if (!getDomain().equalsIgnoreCase(host)) {
			return messageFactory.createResponse(Response.FORBIDDEN, request);
		}
		return null;
	}

	protected Response postProcessRequest(Request request, Response response)
			throws Exception {
		return response;
	}

	protected void postProcessRequestFinal(Request request) {
	}
}
