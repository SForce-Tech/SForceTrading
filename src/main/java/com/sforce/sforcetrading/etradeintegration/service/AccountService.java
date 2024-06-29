package com.sforce.sforcetrading.etradeintegration.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sforce.sforcetrading.etradeintegration.clients.accounts.AccountListClient;
import com.sforce.sforcetrading.etradeintegration.clients.accounts.BalanceClient;
import com.sforce.sforcetrading.etradeintegration.clients.accounts.PortfolioClient;
import com.sforce.sforcetrading.etradeintegration.exception.ApiException;

public class AccountService {

    @Autowired
    private AccountListClient accountListClient;

    @Autowired
    private BalanceClient balanceClient;

    @Autowired
    private PortfolioClient portfolioClient;

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private Map<String, String> acctListMap = new HashMap<>();

    public String getAccountList() {
        String response = "";
        try {
            response = accountListClient.getAccountList();
            parseAccountList(response);
        } catch (ApiException e) {
            log.error("API Exception: HttpStatus: {}, Message: {}, Error Code: {}", e.getHttpStatus(), e.getMessage(),
                    e.getCode());
        } catch (Exception e) {
            log.error("Generic Exception: {}", e.getMessage(), e);
        }
        return response;
    }

    public String getBalance(String accountIdKey) {
        try {
            return balanceClient.getBalance(accountIdKey);
        } catch (ApiException e) {
            log.error("API Exception: HttpStatus: {}, Message: {}, Error Code: {}", e.getHttpStatus(), e.getMessage(),
                    e.getCode());
        } catch (Exception e) {
            log.error("Generic Exception: {}", e.getMessage(), e);
        }
        return null;
    }

    public String getPortfolio(String accountIdKey) {
        try {
            return portfolioClient.getPortfolio(accountIdKey);
        } catch (ApiException e) {
            log.error("API Exception: HttpStatus: {}, Message: {}, Error Code: {}", e.getHttpStatus(), e.getMessage(),
                    e.getCode());
        } catch (Exception e) {
            log.error("Generic Exception: {}", e.getMessage(), e);
        }
        return null;
    }

    private void parseAccountList(String response) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            JSONObject acctLstResponse = (JSONObject) jsonObject.get("AccountListResponse");
            JSONObject accounts = (JSONObject) acctLstResponse.get("Accounts");
            JSONArray acctsArr = (JSONArray) accounts.get("Account");
            Iterator itr = acctsArr.iterator();
            long count = 1;
            while (itr.hasNext()) {
                JSONObject innerObj = (JSONObject) itr.next();
                String acctIdKey = (String) innerObj.get("accountIdKey");
                String acctStatus = (String) innerObj.get("accountStatus");
                if (acctStatus != null && !acctStatus.equals("CLOSED")) {
                    acctListMap.put(String.valueOf(count), acctIdKey);
                    count++;
                }
            }
        } catch (Exception e) {
            log.error("Exception on parsing account list: {}", e.getMessage(), e);
        }
    }

    public String getAccountIdKeyForIndex(String acctIndex) throws ApiException {
        String accountIdKey = acctListMap.get(acctIndex);
        if (accountIdKey == null) {
            throw new ApiException(0, "0", "Invalid selection for accountId index");
        }
        return accountIdKey;
    }
}
