package com.sforce.sforcetrading.etradeintegration.controller;

import com.sforce.sforcetrading.etradeintegration.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @GetMapping("/quotes")
    public String getQuotes(@RequestParam String symbol) {
        return marketService.getQuotes(symbol);
    }
}
