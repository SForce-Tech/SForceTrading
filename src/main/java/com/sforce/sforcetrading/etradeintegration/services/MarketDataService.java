package com.sforce.sforcetrading.etradeintegration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sforce.sforcetrading.etradeintegration.oauth.AuthorizationService;

@Service
public class MarketDataService {
    private final AuthorizationService authorizationService;
    private final RestTemplate restTemplate;

    @Autowired
    public MarketDataService(AuthorizationService authorizationService, RestTemplate restTemplate) {
        this.authorizationService = authorizationService;
        this.restTemplate = restTemplate;
    }

    public String getMarketData(String ticker) {
        String token = authorizationService.getOAuthToken();
        // Use RestTemplate to call eTrade API with the token
        return "MarketData";
    }
}
