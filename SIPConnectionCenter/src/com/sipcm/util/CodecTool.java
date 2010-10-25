/**
 * 
 */
package com.sipcm.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jasypt.util.text.StrongTextEncryptor;

/**
 * @author Jack
 */
public class CodecTool {
	public static final String DefaultProviderClassName = "org.bouncycastle.jce.provider.BouncyCastleProvider";

	public static final String PASSWORD = "P@ssw0rd@S1PCw";

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
		textEncryptor.setPassword(PASSWORD);
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
