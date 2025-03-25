package com.yzlc.common.util;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * Jasypt安全框架加密类工具包
 *<dependency>
 *             <groupId>com.github.ulisesbocchio</groupId>
 *             <artifactId>jasypt-spring-boot-starter</artifactId>
 *             <version>1.18</version>
 *         </dependency>
 * @author Thinking.H
 */
public class JasyptUtils {

    private static final String ENCRYPTED_VALUE_PREFIX = "ENC(";
    private static final String ENCRYPTED_VALUE_SUFFIX = ")";


    /**
     * 判断是否是 prefixes/suffixes 包裹的属性
     *
     * @param value
     * @return
     */
    public static boolean isEncryptedValue(final String value) {
        if (value == null) {
            return false;
        }
        final String trimmedValue = value.trim();
        return (trimmedValue.startsWith(ENCRYPTED_VALUE_PREFIX) &&
                trimmedValue.endsWith(ENCRYPTED_VALUE_SUFFIX));
    }

    /**
     * 如果通过 prefixes/suffixes 包裹的属性，那么返回密文的值；如果没有被包裹，返回原生的值。
     *
     * @param value
     * @return
     */
    private static String getInnerEncryptedValue(final String value) {
        return value.substring(
                ENCRYPTED_VALUE_PREFIX.length(),
                (value.length() - ENCRYPTED_VALUE_SUFFIX.length()));
    }


    /**
     * Jasypt生成加密结果
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待加密值
     * @return
     */
    public static String encryptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptOr = new PooledPBEStringEncryptor();
        encryptOr.setConfig(cryptOr(password));
        return encryptOr.encrypt(value);
    }

    /**
     * 解密
     *
     * @param password 配置文件中设定的加密密码 jasypt.encryptor.password
     * @param value    待解密密文
     * @return
     */
    public static String decryptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptOr = new PooledPBEStringEncryptor();
        encryptOr.setConfig(cryptOr(password));
        return encryptOr.decrypt(isEncryptedValue(value) ? getInnerEncryptedValue(value) : value);
    }

    /**
     * @param password salt
     * @return
     */
    public static SimpleStringPBEConfig cryptOr(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName(null);
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        return config;
    }


    public static void main(String[] args) {
        // 加密
        System.out.println(encryptPwd("XHH2021", "admin123"));

        // 解密一
        System.out.println(decryptPwd("XHH2021", "OyLAKC6iqq5KWszFrl40aKGMySRhiRdQ"));

        // 解密二
        System.out.println(decryptPwd("XHH2021", "ENC(dedxSo9fWlhIbZfrHRHAyoi68Mk/QIdD)"));
    }

}
