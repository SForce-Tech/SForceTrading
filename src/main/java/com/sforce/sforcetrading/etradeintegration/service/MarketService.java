package com.sforce.sforcetrading.etradeintegration.service;

import com.sforce.sforcetrading.etradeintegration.clients.market.QuotesClient;
import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Iterator;

@Service
public class MarketService {

    private static final Logger log = LoggerFactory.getLogger(MarketService.class);

    @Autowired
    private QuotesClient quotesClient;

    public String getQuotes(String symbol) {
        DecimalFormat format = new DecimalFormat("#.00");
        String response = "";
        try {
            response = quotesClient.getQuotes(symbol);
            log.debug(" Response String : " + response);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            log.debug(" JSONObject : " + jsonObject);

            JSONObject quoteResponse = (JSONObject) jsonObject.get("QuoteResponse");
            JSONArray quoteData = (JSONArray) quoteResponse.get("QuoteData");

            if (quoteData != null) {
                StringBuilder output = new StringBuilder();
                Iterator itr = quoteData.iterator();
                while (itr.hasNext()) {
                    JSONObject innerObj = (JSONObject) itr.next();
                    output.append(parseQuoteData(innerObj, format)).append("\n");
                }
                return output.toString();
            } else {
                log.error(" Error : Invalid stock symbol.");
                return "Error : Invalid Stock Symbol.\n";
            }
        } catch (ApiException e) {
            log.error("API Exception: HttpStatus: {}, Message: {}, Error Code: {}", e.getHttpStatus(), e.getMessage(),
                    e.getCode());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
        }
        return null;
    }

    private String parseQuoteData(JSONObject innerObj, DecimalFormat format) {
        StringBuilder result = new StringBuilder();

        if (innerObj != null && innerObj.get("dateTime") != null) {
            result.append("Date Time: ").append(innerObj.get("dateTime")).append("\n");
        }

        JSONObject product = (JSONObject) innerObj.get("Product");
        if (product != null && product.get("symbol") != null) {
            result.append("Symbol: ").append(product.get("symbol")).append("\n");
        }

        if (product != null && product.get("securityType") != null) {
            result.append("Security Type: ").append(product.get("securityType")).append("\n");
        }

        JSONObject all = (JSONObject) innerObj.get("All");
        if (all != null) {
            appendAllQuoteData(result, all, format);
        }

        JSONObject mutualFund = (JSONObject) innerObj.get("MutualFund");
        if (mutualFund != null) {
            appendMutualFundData(result, mutualFund, format);
        }

        return result.toString();
    }

    private void appendAllQuoteData(StringBuilder result, JSONObject all, DecimalFormat format) {
        if (all.get("lastTrade") != null) {
            result.append("Last Price: ").append(all.get("lastTrade")).append("\n");
        }

        if (all.get("changeClose") != null && all.get("changeClosePercentage") != null) {
            result.append("Today's Change: ").append(format.format(all.get("changeClose")))
                    .append(" (").append(all.get("changeClosePercentage")).append("%)\n");
        }

        if (all.get("open") != null) {
            result.append("Open: ").append(all.get("open")).append("\n");
        }

        if (all.get("previousClose") != null) {
            result.append("Previous Close: ").append(format.format(all.get("previousClose"))).append("\n");
        }

        if (all.get("bid") != null && all.get("bidSize") != null) {
            result.append("Bid (Size): ").append(format.format(all.get("bid"))).append("x").append(all.get("bidSize"))
                    .append("\n");
        }

        if (all.get("ask") != null && all.get("askSize") != null) {
            result.append("Ask (Size): ").append(format.format(all.get("ask"))).append("x").append(all.get("askSize"))
                    .append("\n");
        }

        if (all.get("low") != null && all.get("high") != null) {
            result.append("Day's Range: ").append(format.format(all.get("low"))).append("-").append(all.get("high"))
                    .append("\n");
        }

        if (all.get("totalVolume") != null) {
            result.append("Volume: ").append(all.get("totalVolume")).append("\n");
        }
    }

    private void appendMutualFundData(StringBuilder result, JSONObject mutualFund, DecimalFormat format) {
        if (mutualFund.get("netAssetValue") != null) {
            result.append("Net Asset Value: ").append(mutualFund.get("netAssetValue")).append("\n");
        }

        if (mutualFund.get("changeClose") != null && mutualFund.get("changeClosePercentage") != null) {
            result.append("Today's Change: ").append(format.format(mutualFund.get("changeClose")))
                    .append(" (").append(mutualFund.get("changeClosePercentage")).append("%)\n");
        }

        if (mutualFund.get("publicOfferPrice") != null) {
            result.append("Public Offer Price: ").append(mutualFund.get("publicOfferPrice")).append("\n");
        }

        if (mutualFund.get("previousClose") != null) {
            result.append("Previous Close: ").append(mutualFund.get("previousClose")).append("\n");
        }
    }
}
