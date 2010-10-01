/**
 * 
 */
package com.sipcm.util;

import java.security.InvalidKeyException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Jack
 */
public class CodecTool {
	public static final String DefaultProviderClassName = "org.bouncycastle.jce.provider.BouncyCastleProvider";

	private static final String DefaultAlgorithm = "PBEWithSHAAnd128BitRC2-CBC";

	private Cipher eCipher;

	private Cipher dCipher;

	private Provider cryptoProvider;

	private String algorithm;

	public CodecTool() {
	}

	private CodecTool(Provider provider, String algorithm) {
		this.cryptoProvider = provider;
		this.algorithm = algorithm;
	}

	public static CodecTool getInstance(Provider provider, String algorithm) {
		CodecTool instance = new CodecTool(provider, algorithm);
		instance.init();
		return instance;
	}

	/**
	 * Initial algorithm
	 */
	@PostConstruct
	public void init() {
		Security.addProvider(cryptoProvider);
		// 8-byte Salt
		byte[] salt = { (byte) 0x1b, (byte) 0xf3, (byte) 0x2c, (byte) 0x8c,
				(byte) 0x13, (byte) 0x30, (byte) 0x7d, (byte) 0xd2 };
		char[] password = "dk#kniwn%^\\i3,)42b!^dnw2@\"nKnWod".toCharArray();
		int nCount = 19;
		try {
			KeySpec keySpec = new PBEKeySpec(password, salt, nCount);
			SecretKey pbeKey = SecretKeyFactory.getInstance(algorithm)
					.generateSecret(keySpec);
			eCipher = Cipher.getInstance(pbeKey.getAlgorithm());
			dCipher = Cipher.getInstance(pbeKey.getAlgorithm());
			AlgorithmParameterSpec pbeParameterSpec = new PBEParameterSpec(
					salt, nCount);
			try {
				eCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParameterSpec);
				dCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParameterSpec);
			} catch (InvalidKeyException ignore) {

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Encrypt byte array to byte array
	 * 
	 * @param data
	 * @return encrypted byte array
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] data) throws Exception {
		if (data == null) {
			return null;
		}
		return eCipher.doFinal(data);
	}

	/**
	 * Decrypt byte array to byte array
	 * 
	 * @param data
	 * @return decrypted byte array
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] data) throws Exception {
		if (data == null) {
			return null;
		}
		return dCipher.doFinal(data);
	}

	/**
	 * Encrypt String to String
	 * 
	 * @param data
	 * @return encrypted string
	 * @throws Exception
	 */
	public String encrypt(String data) throws Exception {
		if (data == null) {
			return null;
		}
		byte[] cipherText = encrypt(data.getBytes("UTF-8"));
		return new String(Base64.encodeBase64(cipherText), "UTF-8");
	}

	/**
	 * Decrypt String to String
	 * 
	 * @param data
	 * @return decrypted string
	 * @throws Exception
	 */
	public String decrypt(String data) throws Exception {
		if (data == null) {
			return null;
		}
		byte[] cipherText = Base64.decodeBase64(data.getBytes("UTF-8"));
		byte[] clearText = decrypt(cipherText);
		return new String(clearText, "UTF-8");
	}

	/**
	 * How to use the main method
	 * 
	 */
	public static void printUsage() {
		System.out.println("Usage:");
		System.out.println("java com.sipcm.util.CodecTool <-e/-d> word");
		System.out.println("\t -e is to encrypt and -d to decrypt\n");
		System.out.println("Ex.:  CodecTool -e password");
	}

	/**
	 * Main method is being called by the cipherUtil.sh script
	 * 
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		try {

			// java CipherTool -e password > to encrypt
			// java CipherTool -d password > to decrypt
			if (args.length == 0) {
				printUsage();
				System.exit(1);
			}

			Map<String, String> options = new HashMap<String, String>();

			// Build command line options
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-")) {
					if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
						options.put(args[i].substring(1), args[i + 1]);
						i++;
					} else {
						options.put(args[i].substring(1), null);
					}
				}
			}

			if (options.containsKey("?")) {
				printUsage();
				System.exit(0);
			}

			String val = "";
			String encryptWord = (val = options.get("e")) == null ? null : val;

			String decryptWord = (val = options.get("d")) == null ? null : val;

			String processedWord = "";

			Class<Provider> clazz = (Class<Provider>) Class
					.forName(DefaultProviderClassName);

			Provider provider = clazz.newInstance();

			CodecTool cipher = CodecTool
					.getInstance(provider, DefaultAlgorithm);

			if (encryptWord != null)
				processedWord = cipher.encrypt(encryptWord);
			else if (decryptWord != null)
				processedWord = cipher.decrypt(decryptWord);
			else {
				System.err.println("Error: Invalid arguments-> " + args[0]);
				printUsage();
				System.exit(1);
			}

			System.out.println(processedWord);
			System.exit(0);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
