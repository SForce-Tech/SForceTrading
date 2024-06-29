package com.sforce.sforcetrading.etradeintegration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sforce.sforcetrading.etradeintegration.oauth.AuthorizationService;

@Service
public class PositionService {
    private final AuthorizationService authorizationService;
    private final RestTemplate restTemplate;

    @Autowired
    public PositionService(AuthorizationService authorizationService, RestTemplate restTemplate) {
        this.authorizationService = authorizationService;
        this.restTemplate = restTemplate;
    }

    public String getPositions() {
        String token = authorizationService.getOAuthToken();
        // Use RestTemplate to call eTrade API with the token
        return "Positions";
    }
}
