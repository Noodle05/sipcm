/**
 * 
 */
package com.mycallstation.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jasypt.util.text.StrongTextEncryptor;

/**
 * @author Wei Gao
 */
public class CodecTool {
	public static final byte[] PASSWORD = { 0x7a, 0x38, 0x0e, 0x19, 0x3c, 0x0c,
			0x41, 0x21, 0x06, 0x73, 0x34, 0x35, 0x05, 0x05 };

	public static final String OPTION_HELP = "h";
	public static final String OPTION_HELP_LONG = "help";
	public static final String OPTION_ENCRYPT = "e";
	public static final String OPTION_ENCRYPT_LONG = "encrypt";
	public static final String OPTION_DECRYPT = "d";
	public static final String OPTION_DECRYPT_LONG = "decrypt";
	public static final String OPTION_PASSWORD = "p";
	public static final String OPTION_PASSWORD_LONG = "password";

	private static final StrongTextEncryptor textEncryptor = new StrongTextEncryptor();

	static {
		textEncryptor.setPassword(getEncryptPW());
	}

	public static final String getEncryptPW() {
		int length = PASSWORD.length;
		byte[] tmp = new byte[length];
		for (int i = 0; i < tmp.length; i++) {
			byte b = (byte) (PASSWORD[length - i - 1] ^ i);
			b = (byte) (((b & 0x0f) << 4) | ((b & 0xf0) >> 4));
			tmp[i] = b;
		}
		return new String(tmp);
	}

	/**
	 * Main method is being called by the cipherUtil.sh script
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Option oHelp = new Option(OPTION_HELP, OPTION_HELP_LONG, false,
				"print this message");
		Option oEncrypt = new Option(OPTION_ENCRYPT, OPTION_ENCRYPT_LONG, true,
				"encrypt password");
		Option oDecrypt = new Option(OPTION_DECRYPT, OPTION_DECRYPT_LONG, true,
				"decrypt password (default: encrypt password)");
		Option oPasswd = new Option(OPTION_PASSWORD, OPTION_PASSWORD_LONG,
				true, "Alogrithm password");
		Options options = new Options();
		options.addOption(oHelp).addOption(oEncrypt).addOption(oDecrypt)
				.addOption(oPasswd);
		CommandLineParser parser = new GnuParser();

		String encryptString;
		String decryptString;
		String password;
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption(OPTION_HELP)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("CodecTool", options);
				System.exit(0);
			}
			if (line.hasOption(OPTION_ENCRYPT)) {
				encryptString = line.getOptionValue(OPTION_ENCRYPT);
			} else {
				encryptString = null;
			}
			if (line.hasOption(OPTION_DECRYPT)) {
				decryptString = line.getOptionValue(OPTION_DECRYPT);
			} else {
				decryptString = null;
			}
			if (line.hasOption(OPTION_PASSWORD)) {
				password = line.getOptionValue(OPTION_PASSWORD);
			} else {
				password = null;
			}

			if (password != null) {
				textEncryptor.setPassword(password);
			}

			if (encryptString != null) {
				try {
					String encryptedString = textEncryptor
							.encrypt(encryptString);
					System.out.println("\"" + encryptString + "\" --> \""
							+ encryptedString + "\"");
				} catch (Exception e) {
					System.out.println("Error happened when encrypt \""
							+ encryptString + "\". Error message: "
							+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (decryptString != null) {
				try {
					String decryptedString = textEncryptor
							.decrypt(decryptString);
					System.out.println("\"" + decryptedString + "\" <-- \""
							+ decryptString + "\"");
				} catch (Exception e) {
					System.out.println("Error happened when decrypt \""
							+ decryptString + "\". Error message: "
							+ e.getMessage());
					e.printStackTrace();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
