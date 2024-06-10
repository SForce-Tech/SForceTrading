package com.sforce.gymbuddy.util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * Utility class for RSA encryption and decryption.
 * Provides methods to load public and private keys from Base64 encoded strings,
 * and to encrypt and decrypt data using these keys.
 */
public class RSAUtil {

    /**
     * Converts a Base64 encoded public key string into a PublicKey object.
     *
     * @param base64PublicKey the Base64 encoded public key string
     * @return the PublicKey object
     * @throws Exception if an error occurs during key conversion
     */
    public static PublicKey getPublicKey(String base64PublicKey) throws Exception {
        // Remove header, footer, and newlines
        String publicKeyPEM = base64PublicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    /**
     * Converts a Base64 encoded private key string into a PrivateKey object.
     *
     * @param base64PrivateKey the Base64 encoded private key string
     * @return the PrivateKey object
     * @throws Exception if an error occurs during key conversion
     */
    public static PrivateKey getPrivateKey(String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    /**
     * Encrypts data using a public key.
     *
     * @param data      the data to encrypt
     * @param publicKey the public key to use for encryption
     * @return the encrypted data as a byte array
     * @throws Exception if an error occurs during encryption
     */
    public static byte[] encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    /**
     * Decrypts data using a private key.
     *
     * @param data       the data to decrypt
     * @param privateKey the private key to use for decryption
     * @return the decrypted data as a string
     * @throws Exception if an error occurs during decryption
     */
    public static String decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }
}
