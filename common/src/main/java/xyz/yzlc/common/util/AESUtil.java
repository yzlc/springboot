package xyz.yzlc.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * AES加密工具类
 */
public class AESUtil {
    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同
    private static final String KEY = "0000000000000000";
    private static final String IV = "0000000000000000";
    private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";

    public static String encrypt(String value) throws GeneralSecurityException {
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), AES);
        Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key,iv);
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return new BASE64Encoder().encode(encrypted);
    }

    public static String decrypt(String encrypted) throws GeneralSecurityException, IOException {
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), AES);
        Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        byte[] original = cipher.doFinal(new BASE64Decoder().decodeBuffer(encrypted));
        return new String(original);
    }
}