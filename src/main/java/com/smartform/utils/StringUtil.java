package com.smartform.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base32;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StringUtil {
	public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512";
	private static final int SALT_BYTE_SIZE = 64;
	private static final int HASH_BYTE_SIZE = 64;
	private static final int PASSWORD_MIN_SIZE = 8;
	private static final int PASSWORD_MAX_SIZE = 12;
	private static final int PBKDF2_ITERATIONS = 3;
	private static final String CHARS_NUMBER = "0123456789";
	private static final String CHARS_UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CHARS_LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String CHARS_SPECIAL = "!@#$%&*()_-+=[]{}|:/?.,><";
	public static final String SEPARATOR_CODE = "-";
	final static char[] digits = {
			'0' , '1' , '2' , '3' , '4' , '5' ,
			'6' , '7' , '8' , '9' , 'a' , 'b' ,
			'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
			'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
			'o' , 'p' , 'q' , 'r' , 's' , 't' ,
			'u' , 'v' , 'w' , 'x' , 'y' , 'z'
		};
	private MessageDigest digest;
	public static void main(String[] args) {
		int input = 140;
		String s = Int2HexString(input, 4);
		int output = HexString2Int(s);
		String key = "2bda0c28d4585644368c8ddba499513f";
		int N = 200000;
		long start = System.currentTimeMillis();
		for(int i = 0; i < N; i++) {
			String2Byte(key);
		}
		System.out.println(System.currentTimeMillis() - start);
		assert(input == output);
	}
	@Startup
	private void init() {
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getCamelCase(String str, char seperator, boolean upperFirst) {
		StringBuilder builder = new StringBuilder();
		// Flag to keep track if last visited character is a
		// white space or not
		boolean isLastSeperator = upperFirst;

		// Iterate String from beginning to end.
		for(int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if(isLastSeperator && ch >= 'a' && ch <='z') {
				// Character need to be converted to uppercase
				builder.append((char)(ch + ('A' - 'a') ));
				isLastSeperator = false;
			} else if (ch != seperator) {
				builder.append(ch);
				isLastSeperator = false;
			} else {
				isLastSeperator = true;
			}
		}
		return builder.toString();
	}
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}
	public static String getUUID() {
		java.util.UUID temp = java.util.UUID.randomUUID();
		//StringBuilder uuidString = new StringBuilder(Long.toHexString(temp.getMostSignificantBits()));
		//uuidString.append(Long.toHexString(temp.getLeastSignificantBits()));
		//return uuidString.toString();
		return temp.toString();
	}

	public static String Int2HexString(int number, int size) {
		char[] buf = new char[size];
		for(int i = size - 1; i >= 0; i--) {
			buf[i] = digits[number & 0xf];
			number >>= 4;
		}
		return new String(buf);
	}
	public static int HexString2Int(String s) {
		if(s == null || s.isEmpty()) return 0;
		int result = 0;
		int ind = 0;
		while(ind < s.length()) {
			char c = s.charAt(ind++);
			if (c < 'a') {
				result = (result << 4) | (c - 48);
			} else {
				result = (result << 4) | (c - 87);
			}
		}
		return result;
	}
	public static byte[] String2Key(String string) {
		assert string.length() == 32;
		byte[] result = new byte[16];
		int ind = 0;
		for(int i = 0; i < 16 ; i++) {
			char c = string.charAt(ind++);
			int v = 0;
			if (c < 'a') {
				v = (c - 48) << 4;
			} else {
				v = (c - 87) << 4;
			}
			c = string.charAt(ind++);
			if (c < 'a') {
				v |= (c - 48);
			} else {
				v |= (c - 87);
			}
			result[i] = (byte)v;
		}
		return result;
	}
	public static String Byte2String(byte[] bytes) {
		int size = bytes.length << 1;
		char[] buf = new char[size];
		int ind = 0;
		for (int i = 0; i < bytes.length; i++) {
			buf[ind++] = digits[(bytes[i] >> 4) & 0xf];
			buf[ind++] = digits[bytes[i] & 0xf];
		}
		return new String(buf);
	}
	public static byte[] String2Byte(String string) {
		int size = string.length() / 2;
		byte[] result = new byte[size];
		int ind = 0;
		for (int i = 0; i < size; i++) {
			char c = string.charAt(ind++);
			int v = 0;
			if (c < 'a') {
				v = (c - 48) << 4;
			} else {
				v = (c - 87) << 4;
			}
			c = string.charAt(ind++);
			if (c < 'a') {
				v |= (c - 48);
			} else {
				v |= (c - 87);
			}
			result[i] = (byte)v;
		}
		return result;
	}

	public static String bytesToHex(byte[] hash) {
	    StringBuilder hexString = new StringBuilder(2 * hash.length);
	    for (int i = 0; i < hash.length; i++) {
	        String hex = Integer.toHexString(0xff & hash[i]);
	        if(hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
	public static byte[] getSalt() {
		SecureRandom secureRandom;
		secureRandom = new SecureRandom(); // Use the Java secure PRNG
		byte[] salt = new byte[SALT_BYTE_SIZE];
		secureRandom.nextBytes(salt);
		return salt;
	}
	public static byte[] getSalt(int size) {
		SecureRandom secureRandom = new SecureRandom(); // Use the Java secure PRNG
		byte[] salt = new byte[size];
		secureRandom.nextBytes(salt);
		/*
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.nextBytes(salt);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return salt;
	}
	public static String getHashPassword(byte[] salt, String password) {
	    byte[] hash = null;
		SecretKeyFactory skf;
		try {
			skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE * 8);
			hash = skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return hash != null ? Byte2String(hash) : null;
	}
	public static String getSecretToken(int tokenSize) {
		SecureRandom secureRandom = new SecureRandom(); // Use the Java secure PRNG
		byte[] secretKey = new byte[tokenSize];
		secureRandom.nextBytes(secretKey);
		Base32 encoder = new Base32();	//35NAIXQOYBTASVUY
		String encodedKey = new String(encoder.encode(secretKey));
		return encodedKey;
	}
	public static String generate() {
		SecureRandom secureRandom = new SecureRandom();
		int size = secureRandom.nextInt(PASSWORD_MAX_SIZE - PASSWORD_MIN_SIZE) + PASSWORD_MIN_SIZE;
		char[] pwds = new char[size];
		int pos, ind;
		boolean next = true;
		//Upper case
		do {
			ind = secureRandom.nextInt(CHARS_UPPER_CASE.length());
			pos = secureRandom.nextInt(size);
			if(pwds[pos] == 0) {
				pwds[pos] = CHARS_UPPER_CASE.charAt(ind);
				next = false;
			}
		} while(next);
		//Lower case
		next = true;
		do {
			ind = secureRandom.nextInt(CHARS_LOWER_CASE.length());
			pos = secureRandom.nextInt(size);
			if(pwds[pos] == 0) {
				pwds[pos] = CHARS_LOWER_CASE.charAt(ind);
				next = false;
			}
		} while(next);
		//Number
		next = true;
		do {
			ind = secureRandom.nextInt(CHARS_NUMBER.length());
			pos = secureRandom.nextInt(size);
			if(pwds[pos] == 0) {
				pwds[pos] = CHARS_NUMBER.charAt(ind);
				next = false;
			}
		} while(next);
		//Special character
		next = true;
		do {
			ind = secureRandom.nextInt(CHARS_SPECIAL.length());
			pos = secureRandom.nextInt(size);
			if(pwds[pos] == 0) {
				pwds[pos] = CHARS_SPECIAL.charAt(ind);
				next = false;
			}
		} while(next);
		// Generate from overall
		StringBuilder sb = new StringBuilder(CHARS_NUMBER).append(CHARS_UPPER_CASE).append(CHARS_LOWER_CASE).append(CHARS_SPECIAL);
		int length = sb.length();
        for (int i = 0; i < pwds.length; i++) {
            if (pwds[i] > 0) continue;
            pwds[i] = sb.charAt(secureRandom.nextInt(length));
        }
		String pwd = new String(pwds);
		return pwd;
	}
	public static boolean validate(String password) {
		if(password == null || password.length() < PASSWORD_MIN_SIZE) return false;
		boolean hasUpper = false, hasLower = false, hasNumber = false, hasSpecial = false;
		for(int i = 0; i < password.length(); i++) {
			CharSequence cs = password.subSequence(i, i+1);
			if(!hasLower && CHARS_LOWER_CASE.contains(cs)) {
				hasLower = true;
			} else if(!hasNumber && CHARS_NUMBER.contains(cs)) {
				hasNumber = true;
			} else if(!hasSpecial && CHARS_SPECIAL.contains(cs)) {
				hasSpecial = true;
			} else if (!hasUpper && CHARS_UPPER_CASE.contains(cs)) {
				hasUpper = true;
			}
		}
		return hasLower & hasNumber & hasSpecial & hasUpper;
	}
	public String generateHash(String input) {
		byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(encodedhash);
	}
	public static Integer getLastIndex(Collection<String> codes, String seperator) {
		Integer result = 0;
		Integer currentInd = 0;
		for (String code : codes) {
			String[] parts = code.split(seperator);
			if (parts.length >= 2) {
				currentInd = Integer.parseInt(parts[1]);
				if (result < currentInd) {
					result = currentInd;
				}
			}
		}
		return result;
	}
}
