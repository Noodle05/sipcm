package com.sipcm.sip.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PhoneNumberUtilTest {
	private PhoneNumberUtil phoneNumberUtil;

	@Before
	public void init() {
		phoneNumberUtil = new PhoneNumberUtil();
	}

	@Test
	public void testGetCanonicalizedPhoneNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsValidPhoneNumber() {
		assertTrue(phoneNumberUtil.isValidPhoneNumber("+14084763933"));
		assertTrue(phoneNumberUtil.isValidPhoneNumber("14084763933"));
		assertTrue(phoneNumberUtil.isValidPhoneNumber("4763933"));
		assertTrue(phoneNumberUtil.isValidPhoneNumber("+861083942283"));
		assertTrue(phoneNumberUtil.isValidPhoneNumber("011861083942283"));
		assertTrue(phoneNumberUtil.isValidPhoneNumber("00861083942283"));
		assertFalse(phoneNumberUtil.isValidPhoneNumber("001861083942283"));
		assertFalse(phoneNumberUtil.isValidPhoneNumber("0111861083942283"));
		assertFalse(phoneNumberUtil.isValidPhoneNumber("0111a61083942283"));
		assertFalse(phoneNumberUtil.isValidPhoneNumber("011183"));
		assertFalse(phoneNumberUtil.isValidPhoneNumber("+011183"));
	}

	@Test
	public void testIsNaPhoneNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsInternationalPhoneNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCorrectUsCaPhoneNumber() {
		fail("Not yet implemented");
	}

}
