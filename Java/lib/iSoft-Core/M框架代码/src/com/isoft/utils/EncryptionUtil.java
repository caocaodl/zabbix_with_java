package com.isoft.utils;

import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.log4j.Logger;

public class EncryptionUtil {

    /**
     * The encryption algo
     */
    private static final String ALGORITHM     = "DESEde";
    /**
     * The value used to pad the key
     */
    private static final byte   PADDING_VALUE = 0x00;
    /**
     * The length of the key
     */
    private static final byte   KEY_LENGTH    = 24;

    /**
     * The default key
     */
    private static final byte[] DEFAULT_KEY;

    /**
     * The default key as string
     */
    private static final String DEFAULT_STRING_KEY = "Omnia Gallia in tres partes divida est";

    private static Logger logger = Logger.getLogger(EncryptionUtil.class);

    
    static {
        logger.info("Initializing encryption key");
        byte[] key = DEFAULT_STRING_KEY.getBytes();
        DEFAULT_KEY = paddingKey(key, KEY_LENGTH, PADDING_VALUE);
    }

    public static byte[] encrypt(byte[] plainBytes,
                                 byte[] encryptionKey){

        if (plainBytes == null) {
            return null;
        }
        if (encryptionKey == null) {
            throw new IllegalArgumentException("The key can not be null");
        }

        byte[] cipherBytes = null;
        try {
            encryptionKey = paddingKey(encryptionKey, KEY_LENGTH, PADDING_VALUE);

            KeySpec keySpec = new DESedeKeySpec(encryptionKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            cipherBytes = cipher.doFinal(plainBytes);
        } catch (Exception e) {
        	cipherBytes = plainBytes;
        }

        return cipherBytes;
    }


    public static byte[] encrypt(byte[] plainBytes){
        return encrypt(plainBytes, DEFAULT_KEY);
    }

    public static String encrypt(String plainText, byte[] key) {
        if (plainText == null) {
            return null;
        }
        if (key == null) {
            throw new IllegalArgumentException("The key can not be null");
        }

        return new String(Base64.encode(encrypt(plainText.getBytes(), key)));
    }

    public static String encrypt(String plainText) {
        return encrypt(plainText, DEFAULT_KEY);
    }

    public static byte[] decrypt(byte[] encryptedBytes,
                                 byte[] encryptionKey) {

        if (encryptedBytes == null) {
            return null;
        }
        if (encryptionKey == null) {
            throw new IllegalArgumentException("The key can not be null");
        }
        byte[] plainBytes = null;
        try {
            encryptionKey = paddingKey(encryptionKey, KEY_LENGTH, PADDING_VALUE);

            KeySpec keySpec = new DESedeKeySpec(encryptionKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, key);

            plainBytes  = cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
        	plainBytes = encryptedBytes;
        }

        return plainBytes;
    }

    public static byte[] decrypt(byte[] encryptedBytes) {
        return decrypt(encryptedBytes, DEFAULT_KEY);
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null) {
            return null;
        }
        return new String(decrypt(Base64.decode(encryptedText), DEFAULT_KEY));
    }

    public static String decrypt(String encryptedText, byte[] key) {
        if (encryptedText == null) {
            return null;
        }
        return new String(decrypt(Base64.decode(encryptedText), key));
    }

    private static byte[] paddingKey(byte[] b, int len, byte paddingValue) {

        byte[] newValue = new byte[len];

        if (b == null) {
            //
            // The given byte[] is null...returning a new byte[] with the required
            // length
            //
            return newValue;
        }

        if (b.length >= len) {
            System.arraycopy(b, 0, newValue, 0, len);
            return newValue;
        }

        System.arraycopy(b, 0, newValue, 0, b.length);
        Arrays.fill(newValue, b.length, len, paddingValue);
        return newValue;

    }
    
    public static void main(String[] args){
    	System.out.println(EncryptionUtil.encrypt("123456"));
    }
}
