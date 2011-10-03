/**
 * 
 */
package com.mycallstation.googlevoice.result;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wgao
 * 
 */
public class CallResult implements Serializable {
	private static final long serialVersionUID = -3352055815656353726L;

	private static final Logger logger = LoggerFactory
			.getLogger(CallResult.class);

	public static final Pattern resultPattern = Pattern.compile(
			"^\\{\"ok\"\\s*\\:\\s*(false|true)(?:,(.*))?\\}$", Pattern.DOTALL
					+ Pattern.CASE_INSENSITIVE);

	protected final boolean success;
	protected final String rawData;

	public CallResult(String string) {
		Matcher m = resultPattern.matcher(string);
		if (m.matches()) {
			if ("true".equalsIgnoreCase(m.group(1))) {
				if (logger.isTraceEnabled()) {
					logger.trace("Method return \"ok\" : true. Success.");
				}
				success = true;
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Method return \"ok\" : false. Failed.");
				}
				success = false;
			}
			rawData = m.group(2);
			if (logger.isTraceEnabled()) {
				logger.trace("raw data: {}", rawData);
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Result doesn't match.");
			}
			success = false;
			rawData = null;
		}
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @return the rawData
	 */
	public String getRawData() {
		return rawData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (success) {
			return "Success";
		} else {
			return "Failed";
		}
	}
}
