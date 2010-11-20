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
	public static final String INTERNATIONAL_NUMBER = "(?:\\+|011|00)[^1]\\d{7,}";

	public static final Pattern PHONE_NUMBER = Pattern.compile("^((?:"
			+ US_CA_NUMBER + ")|(?:" + INTERNATIONAL_NUMBER + "))$");
	public static final Pattern US_CA_NUMBER_PATTERN = Pattern.compile("^("
			+ US_CA_NUMBER + ")$");
	public static final Pattern INTERNATIONAL_NUMBER_PATTERN = Pattern
			.compile("^(" + INTERNATIONAL_NUMBER + ")$");

	public String getCanonicalizedPhoneNumber(String phoneNumber) {
		return getCanonicalizedPhoneNumber(phoneNumber, null);
	}

	public String getCanonicalizedPhoneNumber(String phoneNumber,
			String defaultAreaCode) {
		// Remove any no "+" or no digital characters.
		String newNumber = phoneNumber.replaceAll("[^\\+|^\\d]", "");
		String ndfa = (defaultAreaCode == null ? null : defaultAreaCode
				.replaceAll("[^\\d]", ""));
		if (PHONE_NUMBER.matcher(newNumber).matches()) {
			if (INTERNATIONAL_NUMBER_PATTERN.matcher(newNumber).matches()) {
				// If using "011" international prefix, replace with "+"
				if (newNumber.startsWith("011")) {
					newNumber = "+" + newNumber.substring(3);
				} else if (newNumber.startsWith("00")) {
					newNumber = "+" + newNumber.substring(2);
				}
			} else if (US_CA_NUMBER_PATTERN.matcher(newNumber).matches()) {
				if (newNumber.length() == 7 && ndfa != null) {
					newNumber = ndfa + newNumber;
				}
				if (newNumber.length() == 10) {
					newNumber = "1" + newNumber;
				}
				if (newNumber.length() == 11) {
					newNumber = "+" + newNumber;
				}
			}
			return newNumber;
		}
		return phoneNumber;
	}

	public boolean isValidPhoneNumber(String phoneNumber) {
		return (PHONE_NUMBER.matcher(phoneNumber).matches());
	}

	public boolean isNaPhoneNumber(String phoneNumber) {
		return (US_CA_NUMBER_PATTERN
				.matcher(getCanonicalizedPhoneNumber(phoneNumber)).matches());
	}

	public boolean isInternationalPhoneNumber(String phoneNumber) {
		return (INTERNATIONAL_NUMBER_PATTERN
				.matcher(getCanonicalizedPhoneNumber(phoneNumber)).matches());
	}
}