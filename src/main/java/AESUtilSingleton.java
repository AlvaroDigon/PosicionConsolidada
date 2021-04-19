import java.util.Base64;

import org.apache.commons.codec.binary.Hex;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtilSingleton {
	private static final String TAG = "AESUtilSingleton";
	private static final int DEFAULT_ITERATIONS = 100;
	private static final int DEFAULT_KEYSIZE = 256;

	private static final AESUtilSingleton instance = new AESUtilSingleton();

	// private constructor to avoid client applications to use constructor
	private AESUtilSingleton() {
	}

	public static AESUtilSingleton getInstance() {
		return instance;
	}


	public String encrypt(String ciphertext, String passphrase, String salt, String iv) {
		return encrypt(ciphertext, passphrase, salt, iv, DEFAULT_ITERATIONS);
	}


	public String encrypt(String text, String passphrase, String salt, String iv, int iterationCount) {
		try {
			SecretKey key = generateKey(salt, passphrase, iterationCount, DEFAULT_KEYSIZE);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] byteToEncrypt = text.getBytes();
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));
			byte[] encryptedTextBytes = cipher.doFinal(byteToEncrypt);
			return new String(Base64.getEncoder().encodeToString(encryptedTextBytes));


		} catch (Exception e) {
			//Log.e(TAG,e.getMessage());
		}
		return null;
	}


	public String decrypt(String ciphertext, String passphrase, String salt, String iv) {
		return decrypt(ciphertext, passphrase, salt, iv, DEFAULT_ITERATIONS);
	}


	public String decrypt(String ciphertext, String passphrase, String salt, String iv, int iterationCount) {
		try {
			SecretKey key = generateKey(salt, passphrase, iterationCount, DEFAULT_KEYSIZE);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));
			byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
			return new String(decrypted, "UTF-8");
		} catch (Exception e) {
			//Log.e(TAG,e.getMessage());
		}
		return null;
	}


	private SecretKey generateKey(String salt, String passphrase, int iterationCount, int keySize) {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), Hex.decodeHex(salt.toCharArray()), iterationCount,
					keySize);
			return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

		} catch (Exception e) {
			//Log.e(TAG,e.getMessage());
		}
		return null;
	}

}
