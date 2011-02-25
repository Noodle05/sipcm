/**
 * 
 */
package com.sipcm.util;

import java.util.Random;

import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("stringUtils")
public class StringUtils {
	private final Random random;

	private static char[] characters = new char[] { 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', '_' };

	public StringUtils() {
		random = new Random();
	}

	public String generateRandomString(int length) {
		char[] chs = new char[length];
		for (int i = 0; i < length; i++) {
			chs[i] = characters[random.nextInt(characters.length)];
		}
		return new String(chs);
	}
}
