package com.sforce.gymbuddy.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for providing a placeholder endpoint.
 * This endpoint serves as a placeholder to prevent unnecessary network calls
 * and errors
 * in the application. It returns a basic JSON object indicating that the
 * endpoint is a dummy.
 */
@RestController
public class PlaceholderController {

    private static final Logger logger = LoggerFactory.getLogger(PlaceholderController.class);
    private final Bucket bucket;

    /**
     * Constructs a new PlaceholderController and initializes the rate limiting
     * bucket.
     *
     * @param bucket the rate limiting bucket
     */
    public PlaceholderController(Bucket bucket) {
        this.bucket = bucket;
    }

    /**
     * Retrieves placeholder data.
     * This endpoint is used to prevent errors when authData is not available and
     * ensures the useApi hook is always called.
     *
     * @return a ResponseEntity containing placeholder data
     */
    @GetMapping("/api/placeholder")
    public ResponseEntity<Map<String, Object>> getPlaceholderData() {
        if (bucket.tryConsume(1)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "This is a placeholder endpoint");
            response.put("timestamp", LocalDateTime.now());
            response.put("data", "sample data");

            // Log access to this endpoint
            logger.info("Placeholder endpoint accessed at " + LocalDateTime.now());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
    }
}

/**
 * Configuration class for setting up rate limiting.
 */
@Configuration
class RateLimitingConfig {

    /**
     * Creates a Bucket for rate limiting with a limit of 10 requests per minute.
     *
     * @return a configured Bucket instance
     */
    @Bean
    public Bucket bucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
