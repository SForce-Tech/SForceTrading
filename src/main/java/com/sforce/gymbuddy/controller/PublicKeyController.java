package com.sforce.gymbuddy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/public-key")
public class PublicKeyController {

    @Value("${public.key.path}")
    private String publicKeyPath;

    /**
     * Retrieves the public key.
     *
     * @return a ResponseEntity containing the public key that client apps will need
     *         to use to encrypt users passwords
     */
    @GetMapping
    public ResponseEntity<String> getPublicKey() {
        try {
            ClassPathResource resource = new ClassPathResource(publicKeyPath);
            String publicKey = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return ResponseEntity.ok(publicKey);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading public key");
        }
    }
}
