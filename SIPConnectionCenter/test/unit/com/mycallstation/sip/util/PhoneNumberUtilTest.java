package com.mycallstation.sip.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.mycallstation.sip.util.PhoneNumberUtil;

public class PhoneNumberUtilTest {

	@Test
	public void testGetCanonicalizedPhoneNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsValidPhoneNumber() {
		assertTrue(PhoneNumberUtil.isValidPhoneNumber("+14084763933"));
		assertTrue(PhoneNumberUtil.isValidPhoneNumber("14084763933"));
		assertTrue(PhoneNumberUtil.isValidPhoneNumber("4085487389"));
		assertTrue(PhoneNumberUtil.isValidPhoneNumber("4763933"));
		assertTrue(PhoneNumberUtil.isValidPhoneNumber("+861083942283"));
		assertTrue(PhoneNumberUtil.isValidPhoneNumber("011861083942283"));
		assertTrue(PhoneNumberUtil.isValidPhoneNumber("00861083942283"));
		assertFalse(PhoneNumberUtil.isValidPhoneNumber("001861083942283"));
		assertFalse(PhoneNumberUtil.isValidPhoneNumber("0111861083942283"));
		assertFalse(PhoneNumberUtil.isValidPhoneNumber("0111a61083942283"));
		assertFalse(PhoneNumberUtil.isValidPhoneNumber("011183"));
		assertFalse(PhoneNumberUtil.isValidPhoneNumber("+011183"));
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
