package com.digia.monitoring.sonicmq.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides methods for calculating message digests.
 * @author Sami Pajunen
 */
public class DigestUtil {
	
	/** Hexadecimal characters. */
	private static final String HEX = "0123456789abcdef";
	
	/**
	 * Generates SHA1 in hexadecimal format for given text.
	 * @param text Text
	 * @return SHA1 in hexadecimal format
	 */
	public static String sha1hex(String text) {
		return hex(digest(text, "sha1", Charset.forName("utf-8")));
	}

	/**
	 * Digests given text using algorithm and text charset.
	 * @param text Text
	 * @param alg Hash algorithm
	 * @param charset Charset
	 * @return Message digest
	 */
	public static byte[] digest(String text, String alg, Charset charset) {
		try {
			MessageDigest digest = MessageDigest.getInstance(alg);
			return digest.digest(text.getBytes(charset));
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Unable to generate hash.", ex);
		}
	}

	/**
	 * Converts given byte array into hexadecimal string
	 * @param data Data to convert
	 * @return Hexadecimal string
	 */
	public static String hex(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < data.length ; i++) {
			int value = data[i];
			sb.append(HEX.charAt(value >> 4 & 0xf));
			sb.append(HEX.charAt(value & 0xf));
		}
		return sb.toString();
	}
}
