/**
 * 
 */
package com.mycallstation.googlevoice.result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycallstation.googlevoice.util.Utility;

/**
 * @author wgao
 * 
 */
public class EditPhoneResult extends CallResult {
	private static final long serialVersionUID = 2377880720694146738L;

	private static final Logger logger = LoggerFactory
			.getLogger(EditPhoneResult.class);

	public static final Pattern resultPattern = Pattern.compile(
			"^\\s*\"data\"\\:\\s*(\\{.*\\})\\s*$", Pattern.DOTALL
					+ Pattern.CASE_INSENSITIVE);

	private final PhoneResult phone;

	public EditPhoneResult(String string) {
		super(string);
		if (success) {
			Matcher m = resultPattern.matcher(rawData);
			if (m.matches()) {
				String str = m.group(1);
				if (str != null) {
					if (logger.isTraceEnabled()) {
						logger.trace("phone string: {}", str);
					}
					phone = Utility.getGson().fromJson(str, PhoneResult.class);
				} else {
					if (logger.isTraceEnabled()) {
						logger.trace("Phone data is empty.");
					}
					phone = null;
				}
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Cannot find phone data.");
				}
				phone = null;
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Result is failed.");
			}
			phone = null;
		}
	}

	/**
	 * @return the phone
	 */
	public PhoneResult getPhone() {
		return phone;
	}
}
