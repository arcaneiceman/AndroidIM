package at.vcity.androidim;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class CryptoTGTUtils {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	private static String Mode="";

	public static byte[] encrypt(String key, String input) throws Exception {
		Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(input.getBytes("UTF-8"));
	}

	public static String decrypt(String key, byte[] input) throws Exception {
		Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] temp = cipher.doFinal(input);
		return new String(temp, "UTF-8");
	}

}
