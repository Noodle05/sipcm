/**
 * 
 */
package com.mycallstation.util;

import java.util.Arrays;
import java.util.List;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.mycallstation.googlevoice.HttpResponseException;

/**
 * @author Wei Gao
 * 
 */
public abstract class HttpUtils {
	public static String getCharset(HttpEntity entity) {
		String charset = null;
		if (entity != null && entity.getContentType() != null) {
			HeaderElement values[] = entity.getContentType().getElements();
			if (values.length > 0) {
				NameValuePair param = values[0].getParameterByName("charset");
				if (param != null) {
					charset = param.getValue();
				}
			}
		}
		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}
		return charset;
	}

	public static String prepareGetUrl(String url, List<NameValuePair> params) {
		String query = null;
		if (params != null && !params.isEmpty()) {
			query = URLEncodedUtils.format(params, "UTF-8");
		}
		StringBuilder sb = new StringBuilder(url);
		if (query != null) {
			sb.append("?").append(query);
		}
		return sb.toString();
	}

	public static void checkResponse(HttpUriRequest request,
			HttpResponse response, Logger logger) throws HttpResponseException {
		checkResponse(request, response, new int[] { HttpStatus.SC_OK }, logger);
	}

	public static void checkResponse(HttpUriRequest request,
			HttpResponse response, int[] validStatusCode, Logger logger)
			throws HttpResponseException {
		Arrays.sort(validStatusCode);
		if (Arrays.binarySearch(validStatusCode, response.getStatusLine()
				.getStatusCode()) < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Get error, status code {}, reason: {}", response
						.getStatusLine().getStatusCode(), response
						.getStatusLine().getReasonPhrase());
				if (logger.isTraceEnabled()) {
					try {
						logger.trace("Http response body:\n{}",
								EntityUtils.toString(response.getEntity()));
					} catch (Exception e) {
						if (logger.isWarnEnabled()) {
							logger.warn(
									"Error happened when try to print out response content.",
									e);
						}
					}
				}
			}
			request.abort();
			throw new HttpResponseException(response.getStatusLine()
					.getStatusCode(), response.getStatusLine()
					.getReasonPhrase());
		}
	}
}
