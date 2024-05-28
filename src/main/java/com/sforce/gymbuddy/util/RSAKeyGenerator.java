package com.sforce.gymbuddy.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Utility class for generating RSA key pairs.
 * This class generates a public and private RSA key pair and prints them in
 * Base64 encoding.
 */
public class RSAKeyGenerator {
    private static final int KEY_SIZE = 2048;

    /**
     * Main method to generate and print RSA key pairs.
     *
     * @param args command line arguments (not used)
     * @throws Exception if an error occurs during key pair generation
     */
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(KEY_SIZE);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        System.out.println("Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.out.println("Private Key: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
    }
}
