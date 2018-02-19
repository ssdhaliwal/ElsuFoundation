package elsu.common;

import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.commons.codec.binary.*;

public class EncryptionUtils {

	// http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example
	public static String encrypt(String key, String initVector, String value) {
		try {
			// 20171111 - if len < 8, null
			int len = key.length();
			if (len < 8) {
				return null;
			}
			
			// 20171111 - fix key; if > 32 = 32, > 16 = 16, or > 8 = 8
			if (len > 32) {
				key = key.substring(0, 32);
			} else if (len >= 16) {
				key = key.substring(0, 16);
			} else {
				key = key.substring(0, 8);
			}
			
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String decrypt(String key, String initVector, String encrypted) {
		try {
			// 20171111 - if len < 8, null
			int len = key.length();
			if (len < 8) {
				return null;
			}
			
			// 20171111 - fix key; if > 32 = 32, > 16 = 16, or > 8 = 8
			if (len > 32) {
				key = key.substring(0, 32);
			} else if (len >= 16) {
				key = key.substring(0, 16);
			} else {
				key = key.substring(0, 8);
			}
			
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
