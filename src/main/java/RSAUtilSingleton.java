import java.util.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by jogan1075 on 20-06-17.
 */

public class RSAUtilSingleton {

	private static final String TAG = "RSAUtilSingleton";
	private static final String rsaPublicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4b2L3Gk8Q+qlcFQSRmho"
			+ "/fDPvrZ3A5jx3/9XE2X7N6EO2F9RcH23b8EY8hEEd7lHNXqHDYcpt97QPgey1lTq"
			+ "NItUrRqYFACU+5FFxUD6hwcibZFRaBy8PjHsfT2m9VcTlCAvViybHrwWMH73o9V8"
			+ "zLJfoFtxKKxfiTv4CBVIxLN+dTOJbqKgOYSRCG/3022rWcB4KpUqQlqTfp+3CreI"
			+ "IOcTVq9MZ41MvniMczBdC4ywOtBm/sd9acpUch2MKUbEe8nL9izfjahS6MADFAHq"
			+ "DTStCAsfPwh5S3yOj0cVtun6rkMhPG1Av6HT1bAWW8Du6BvF1ZKdC4ZY4t3/e005" + "oQIDAQAB";

	private static PublicKey rsaPublicKey = null;
	private static Cipher cipher = null;

	private static final RSAUtilSingleton instance = new RSAUtilSingleton();

	// private constructor to avoid client applications to use constructor
	private RSAUtilSingleton() {
	}

	public static RSAUtilSingleton getInstance() {
		loadPublicKey();
		return instance;
	}

	private static void loadPublicKey() {

		byte[] publicBytes = Base64.getDecoder().decode(rsaPublicKeyString);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			rsaPublicKey = keyFactory.generatePublic(keySpec);
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException e) {
			//Log.e(TAG,e.getMessage());
		} catch (InvalidKeySpecException e) {
			//Log.e(TAG,e.getMessage());
		} catch (Exception e) {
			//Log.e(TAG,e.getMessage());
		}
	}

	public String encryptValue(String textToEncrypt) {
		String textEncrypted = "";

		try {
			cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
			byte[] bit = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
			textEncrypted = Base64.getEncoder().encodeToString(bit);
		} catch (Exception e) {
			//Log.e(TAG,e.getMessage());
		}

		return textEncrypted;
	}

	public String decryptValue(String textToDecrypt) {
		String textDecrypted = "";
		try {
			cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
			textDecrypted = new String(cipher.doFinal(Base64.getDecoder().decode(textToDecrypt)), "UTF-8");
		} catch (Exception e) {
			//Log.e(TAG,e.getMessage());
		}

		return textDecrypted;
	}

}
