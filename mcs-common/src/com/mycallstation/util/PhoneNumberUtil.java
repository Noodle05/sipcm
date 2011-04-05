/**
 * 
 */
package com.mycallstation.util;

import java.util.regex.Pattern;

/**
 * @author wgao
 * 
 */
public abstract class PhoneNumberUtil {
	private static final long serialVersionUID = -4401623664272801936L;

	public static final String US_CA_NUMBER = "\\d{7}|(?:\\+1|1)?\\d{10}";
	public static final String INTERNATIONAL_NUMBER = "(?:\\+|011|00)[^1]\\d{7,}";

	public static final Pattern PHONE_NUMBER = Pattern.compile("^((?:"
			+ US_CA_NUMBER + ")|(?:" + INTERNATIONAL_NUMBER + "))$");
	public static final Pattern US_CA_NUMBER_PATTERN = Pattern.compile("^("
			+ US_CA_NUMBER + ")$");
	public static final Pattern INTERNATIONAL_NUMBER_PATTERN = Pattern
			.compile("^(" + INTERNATIONAL_NUMBER + ")$");
	public static final Pattern NA_AREA_CODE_PATTERN = Pattern
			.compile("^\\((\\d{3})\\)$");

	public static String getCanonicalizedPhoneNumber(String phoneNumber) {
		return getCanonicalizedPhoneNumber(phoneNumber, null);
	}

	public static String getCanonicalizedPhoneNumber(String phoneNumber,
			String defaultAreaCode) {
		// Remove any no "+" or no digital characters.
		String newNumber = phoneNumber.replaceAll("[^\\+|^\\d]", "");
		String ndfa = (defaultAreaCode == null ? null : defaultAreaCode
				.replaceAll("[^\\d]", ""));
		if (PHONE_NUMBER.matcher(newNumber).matches()) {
			if (INTERNATIONAL_NUMBER_PATTERN.matcher(newNumber).matches()) {
				// If using "011" or "00" international prefix, replace with "+"
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

	public static String getDigitalPhoneNumber(String phoneNumber) {
		return getDigitalPhoneNumber(phoneNumber, null);
	}

	public static String getDigitalPhoneNumber(String phoneNumber,
			String defaultAreaCode) {
		// Remove any no "+" or no digital characters.
		String newNumber = phoneNumber.replaceAll("[^\\+|^\\d]", "");
		String ndfa = (defaultAreaCode == null ? null : defaultAreaCode
				.replaceAll("[^\\d]", ""));
		if (PHONE_NUMBER.matcher(newNumber).matches()) {
			if (INTERNATIONAL_NUMBER_PATTERN.matcher(newNumber).matches()) {
				// If using "+" or "00" international prefix, replace with "+"
				if (newNumber.startsWith("+")) {
					newNumber = "011" + newNumber.substring(1);
				} else if (newNumber.startsWith("00")) {
					newNumber = "011" + newNumber.substring(2);
				}
			} else if (US_CA_NUMBER_PATTERN.matcher(newNumber).matches()) {
				if (newNumber.length() == 7 && ndfa != null) {
					newNumber = ndfa + newNumber;
				}
				if (newNumber.length() == 10) {
					newNumber = "1" + newNumber;
				}
				if (newNumber.startsWith("+")) {
					newNumber = newNumber.substring(1);
				}
			}
			return newNumber;
		}
		return phoneNumber;
	}

	public static boolean isValidPhoneNumber(String phoneNumber) {
		return (PHONE_NUMBER.matcher(phoneNumber).matches());
	}

	public static boolean isNaPhoneNumber(String phoneNumber) {
		return (US_CA_NUMBER_PATTERN
				.matcher(getCanonicalizedPhoneNumber(phoneNumber)).matches());
	}

	public static boolean isInternationalPhoneNumber(String phoneNumber) {
		return (INTERNATIONAL_NUMBER_PATTERN
				.matcher(getCanonicalizedPhoneNumber(phoneNumber)).matches());
	}

	public static String formattedNAPhoneNumber(String phoneNumber) {
		String pn = phoneNumber;
		if (isNaPhoneNumber(phoneNumber)) {
			String t = getCanonicalizedPhoneNumber(phoneNumber).substring(2);
			pn = "(" + t.substring(0, 3) + ") " + t.substring(3, 6) + "-"
					+ t.substring(6);
		}
		return pn;
	}
}
