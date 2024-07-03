package com.sforce.sforcetrading.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OAuth properties.
 */
@Configuration
public class OAuthConfig {

    @Value("${oauth.baseUrl}")
    private String baseUrl;

    @Value("${oauth.authorizeUrl}")
    private String authorizeUrl;

    @Value("${oauth.accessUrl}")
    private String accessUrl;

    @Value("${oauth.tokenUrl}")
    private String tokenUrl;

    @Value("${oauth.consumerKey}")
    private String consumerKey;

    @Value("${oauth.secretKey}")
    private String secretKey;

    @Value("${oauth.callback}")
    private String callback;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getCallback() {
        return callback;
    }
}
