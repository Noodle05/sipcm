/**
 * 
 */
package com.mycallstation.googlevoice.result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wei Gao
 * 
 */
public class CheckForwardingVerifiedResult extends CallResult {
	private static final long serialVersionUID = -8597963101794347228L;

	private static final Logger logger = LoggerFactory
			.getLogger(CheckForwardingVerifiedResult.class);

	public static final Pattern resultPattern = Pattern
			.compile(
					"^\\s*\"verified\"\\:\\s*(false|true),\\s*\"diversionNum\"\\:\\s*(.+),\\s*\"carrier\"\\:\\s*(.+)\\s*$",
					Pattern.DOTALL + Pattern.CASE_INSENSITIVE);

	private final boolean verified;
	private final String diversionNum;
	private final String carrier;

	public CheckForwardingVerifiedResult(String string) {
		super(string);
		if (success) {
			Matcher m = resultPattern.matcher(rawData);
			if (m.matches()) {
				if ("true".equalsIgnoreCase(m.group(1))) {
					verified = true;
				} else {
					verified = false;
				}
				diversionNum = m.group(2);
				carrier = m.group(3);
			} else {
				verified = false;
				diversionNum = null;
				carrier = null;
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Result is failed.");
			}
			verified = false;
			diversionNum = null;
			carrier = null;
		}
	}

	/**
	 * @return the verified
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * @return the diversionNum
	 */
	public String getDiversionNum() {
		return diversionNum;
	}

	/**
	 * @return the carrier
	 */
	public String getCarrier() {
		return carrier;
	}

}
