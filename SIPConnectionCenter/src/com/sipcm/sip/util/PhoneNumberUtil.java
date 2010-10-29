/**
 * 
 */
package com.sipcm.sip.util;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("phoneNumberUtil")
public class PhoneNumberUtil {
	public static final String US_CA_NUMBER = "\\d{7}|(?:\\+1|1)?\\d{10}";
	public static final String INTERNATIONAL_NUMBER = "(?:\\+|011)[^1]\\d{7,}";

	public static final Pattern PHONE_NUMBER = Pattern.compile("^((?:"
			+ US_CA_NUMBER + ")|(?:" + INTERNATIONAL_NUMBER + "))$");
	public static final Pattern US_CA_NUMBER_PATTERN = Pattern.compile("^("
			+ US_CA_NUMBER + ")$");
	public static final Pattern INTERNATIONAL_NUMBER_PATTERN = Pattern
			.compile("^(" + INTERNATIONAL_NUMBER + ")$");

	public String getCanonicalizedPhoneNumber(String phoneNumber) {
		if (PHONE_NUMBER.matcher(phoneNumber).matches()) {
			// Remove any no "+" or no digital characters.
			String newNumber = phoneNumber.replaceAll("[^\\+|^\\d]", "");
			// If using "011" international prefix, replace with "+"
			if (newNumber.startsWith("011")) {
				newNumber = "+" + newNumber.substring(3);
			}
			return newNumber;
		}
		return phoneNumber;
	}

	public boolean isUsCaPhoneNumber(String phoneNumber) {
		return (US_CA_NUMBER_PATTERN.matcher(phoneNumber).matches());
	}

	public boolean isInternationalPhoneNumber(String phoneNumber) {
		return (INTERNATIONAL_NUMBER_PATTERN.matcher(phoneNumber).matches());
	}

	public String getCorrectUsCaPhoneNumber(String phoneNumber,
			String defaultArea) {
		if (isUsCaPhoneNumber(phoneNumber)) {
			String ret = getCanonicalizedPhoneNumber(phoneNumber);
			if (ret.length() == 7 && defaultArea != null) {
				// We need to add area code
				ret = defaultArea + ret;
			}
			if (ret.length() == 10) {
				// Add country code "1"
				ret = "1" + ret;
			}
			return ret;
		}
		return phoneNumber;
	}
}
