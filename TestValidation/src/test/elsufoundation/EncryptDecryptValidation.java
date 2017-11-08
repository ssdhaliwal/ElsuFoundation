package test.elsufoundation;

import elsu.common.*;

public class EncryptDecryptValidation {

	public static void main(String[] args) throws Exception {
		// http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example
		String key = "9@ieRjfn9#205(84"; // 128 bit key
		String initVector = "DmsfWoggkDDorjHr"; // 16 bytes IV
		String data = "NAISPWD";
		String encryptedData = EncryptionUtils.encrypt(key, initVector, data);
		
		System.out.println(encryptedData);
		System.out.println(EncryptionUtils.decrypt(key, initVector, encryptedData));
		
		System.in.read();
	}

}
