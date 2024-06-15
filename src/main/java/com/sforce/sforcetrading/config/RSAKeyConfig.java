package com.sforce.sforcetrading.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * Configuration class for loading the RSA private key used for encryption and
 * decryption.
 */
@Configuration
public class RSAKeyConfig {

    @Value("${rsa.private.key.path}")
    private String privateKeyPath;

    /**
     * Loads the RSA private key from the specified file path.
     *
     * @return the RSA private key
     * @throws Exception if an error occurs while reading the key file or generating
     *                   the private key
     */
    @Bean
    public PrivateKey privateKey() throws Exception {
        // Load the key file from the classpath
        Resource resource = new ClassPathResource(privateKeyPath);
        byte[] keyBytes = Files.readAllBytes(resource.getFile().toPath());

        // Convert the key bytes into a string and clean it up
        String privateKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        // Decode the Base64 string
        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);

        // Create the private key
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
