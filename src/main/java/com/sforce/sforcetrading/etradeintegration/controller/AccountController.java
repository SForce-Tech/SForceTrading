package com.sforce.sforcetrading.etradeintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sforce.sforcetrading.etradeintegration.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/list")
    public String getAccountList() {
        return accountService.getAccountList();
    }

    @GetMapping("/balance")
    public String getBalance(@RequestParam String accountIdKey) {
        return accountService.getBalance(accountIdKey);
    }

    @GetMapping("/portfolio")
    public String getPortfolio(@RequestParam String accountIdKey) {
        return accountService.getPortfolio(accountIdKey);
    }
}
