package fr.skyost.serialkey.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CryptoUtils {
	
	private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, };
	
	public static final String encrypt(final String property, final String password) throws GeneralSecurityException, UnsupportedEncodingException {
		final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
		final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new BASE64Encoder().encode(pbeCipher.doFinal(property.getBytes(StandardCharsets.UTF_8)));
    }

	public static final String decrypt(final String property, final String password) throws GeneralSecurityException, IOException {
		final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		final SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
		final Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(new BASE64Decoder().decodeBuffer(property)), StandardCharsets.UTF_8);
    }
	
}