package com.sforce.sforcetrading.service;

import com.google.api.client.auth.oauth.*;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.sforce.sforcetrading.config.OAuthConfig;
import com.sforce.sforcetrading.exception.OAuthServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling OAuth 1.0a authorization with E*TRADE.
 */
@Service
public class OAuthService {

    private static final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();

    @Autowired
    private OAuthConfig oAuthConfig;

    /**
     * Retrieves the OAuth request token and constructs the authorization URL.
     *
     * @return the authorization URL to redirect the user to.
     * @throws OAuthServiceException if an error occurs while getting the request
     *                               token.
     */
    public String getRequestToken() throws OAuthServiceException {
        try {
            OAuthHmacSigner signer = new OAuthHmacSigner();
            signer.clientSharedSecret = oAuthConfig.getSecretKey();

            String requestTokenUrl = oAuthConfig.getBaseUrl() + oAuthConfig.getTokenUrl();

            OAuthGetTemporaryToken requestToken = new OAuthGetTemporaryToken(requestTokenUrl);
            requestToken.consumerKey = oAuthConfig.getConsumerKey();
            requestToken.signer = signer;
            requestToken.transport = HTTP_TRANSPORT;
            requestToken.callback = oAuthConfig.getCallback();

            OAuthCredentialsResponse response = requestToken.execute();

            String authorizationUrl = oAuthConfig.getAuthorizeUrl() + "?key=" + oAuthConfig.getConsumerKey() + "&token="
                    + response.token;
            return authorizationUrl;
        } catch (HttpResponseException e) {
            throw new OAuthServiceException("Failed to get request token: " + e.getStatusMessage(), e);
        } catch (Exception e) {
            throw new OAuthServiceException("Failed to get request token", e);
        }
    }
}
