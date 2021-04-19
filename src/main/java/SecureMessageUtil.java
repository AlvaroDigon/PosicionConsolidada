import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.security.SecureRandom;

/**
 * Created by jogan1075 on 29-06-17.
 */

public class SecureMessageUtil {
	private static final String TAG = "SecureMessageUtil";
	private AESUtilSingleton aesUtil = AESUtilSingleton.getInstance();
	private RSAUtilSingleton rsaUtil = RSAUtilSingleton.getInstance();

	public String resolveMessage(String secureMessage, String password) {
		try {
			return resolveSecureMessage(secureMessage, password).getM();
		} catch (Exception e) {
			//Log.e(TAG,e.getMessage());
		}
		return null;
	}


	public SecureMessage resolveSecureMessage(String secureMessage, String password) {
		if (secureMessage != null && !"".equals(secureMessage)) {
			try {
				JSONObject jsonSecureMessage = new JSONObject(secureMessage);
				String msg = (String) jsonSecureMessage.get("m");
				String salt = (String) jsonSecureMessage.get("s");
				String iv = (String) jsonSecureMessage.get("i");
				msg = aesUtil.decrypt(msg, password, salt, iv);

				return new SecureMessage(msg, password, salt, iv);

			} catch (Exception e) {
				//Log.e(TAG,e.getMessage());
			}
		} else {
			// Log.e(TAG,"Can't resolve SecureMessage. Invalid (void) parameter!");
		}
		return null;
	}

	/**
	 * Recibe un mensaje plano, y los atributos necesarios para generar un mensaje
	 * de response seguro (password, salt, iv). Devuelve un objeto @SecureMessage
	 * con el mensaje cifrado (AES-256), y el resto de los atributos "salt" e "iv".
	 * Importante: El mensaje seguro de respuesta NO expone la password (vacio).
	 *
	 * @param message
	 * @param password
	 * @param salt
	 * @param iv
	 * @return @SecureMessage
	 */
	public SecureMessage createSecureMessage(String message, String password, String salt, String iv) {
		if (message != null && !"".equals(message)) {
			try {
				return createSecureMessage(message, password, rsaUtil.encryptValue(password), salt, iv);
			} catch (Exception e) {
				//Log.e(TAG,"Can't resolve SecureMessage. Invalid (void) parameter!");
			}
		}
		return null;
	}

	/**
	 * Recibe un mensaje plano, y los atributos necesarios para generar un mensaje
	 * de response seguro (password, salt, iv). Devuelve un objeto @SecureMessage
	 * con el mensaje cifrado (AES-256), y el resto de los atributos "salt" e "iv".
	 * Importante: El mensaje seguro de respuesta NO expone la password (vacio).
	 *
	 * @param message
	 * @param password
	 * @param passwordEncrypted
	 * @param salt
	 * @param iv
	 * @return @SecureMessage
	 */
	public SecureMessage createSecureMessage(String message, String password, String passwordEncrypted, String salt,
                                             String iv) {
		if (message != null && !"".equals(message)) {
			try {
				return new SecureMessage(aesUtil.encrypt(message, password, salt, iv), passwordEncrypted, salt, iv);
			} catch (Exception e) {
				//Log.e(TAG,"Can't resolve SecureMessage. Invalid (void) parameter!");
			}
		}
		return null;
	}

	public String getRandom(int bytes) {
		byte[] res = new byte[bytes];
		SecureRandom random = new SecureRandom();
		random.nextBytes(res);
		return new String(Hex.encodeHex(res));
	}
}
