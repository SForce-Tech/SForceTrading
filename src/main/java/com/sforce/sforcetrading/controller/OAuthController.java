package com.sforce.sforcetrading.controller;

import com.sforce.sforcetrading.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling OAuth-related endpoints.
 */
@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    /**
     * Endpoint to initiate the OAuth authorization process and get the
     * authorization URL.
     *
     * @return the authorization URL in the response body.
     */
    @GetMapping("/request-token")
    public ResponseEntity<Map<String, String>> getRequestToken() {
        try {
            String authorizeUrl = oAuthService.getRequestToken();
            Map<String, String> response = new HashMap<>();
            response.put("authorizeUrl", authorizeUrl);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error occurred while fetching request token: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to handle the OAuth callback with the verifier.
     *
     * @param verifier the OAuth verifier received from E*TRADE.
     * @return the verifier in the response body for demonstration purposes.
     */
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> handleCallback(@RequestParam("oauth_verifier") String verifier) {
        // Here, you would typically exchange the verifier for an access token
        // For now, we will just log it and return it in the response for demonstration
        // purposes
        Map<String, String> response = new HashMap<>();
        response.put("oauth_verifier", verifier);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
