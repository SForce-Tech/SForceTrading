package com.sforce.sforcetrading.etradeintegration.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sforce.sforcetrading.etradeintegration.clients.order.OrderUtil;
import com.sforce.sforcetrading.etradeintegration.clients.order.PriceType;
import com.sforce.sforcetrading.etradeintegration.clients.order.OrderTerm;
import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import com.sforce.sforcetrading.etradeintegration.oauth.AppController;
import com.sforce.sforcetrading.etradeintegration.oauth.model.ApiResource;
import com.sforce.sforcetrading.etradeintegration.oauth.model.ContentType;
import com.sforce.sforcetrading.etradeintegration.oauth.model.Message;
import com.sforce.sforcetrading.etradeintegration.oauth.model.OauthRequired;

@Service
public class OrderPreviewService {

    @Autowired
    private AppController oauthManager;

    @Autowired
    private ApiResource apiResource;

    @Inject
    private VelocityEngine velocityEngine;

    public Map<String, String> getOrderDataMap() {
        Map<String, String> map = new HashMap<>();
        map.put("ORDER_TYPE", "EQ");
        map.put("CLIENT_ID", UUID.randomUUID().toString().substring(0, 8));
        map.put("PRICE_TYPE", "");
        map.put("ORDER_TERM", "");
        map.put("MARKET_SESSION", "REGULAR");
        map.put("STOP_VALUE", "");
        map.put("LIMIT_PRICE", "");
        map.put("SECURITY_TYPE", "EQ");
        map.put("SYMBOL", "");
        map.put("ACTION", "");
        map.put("QUANTITY_TYPE", "QUANTITY");
        map.put("QUANTITY", "");
        return map;
    }

    public String previewOrder(final String accountIdKey, Map<String, String> inputs) throws ApiException {
        String response;
        String requestJson;
        try {
            Template t = velocityEngine.getTemplate("orderpreview.vm");

            VelocityContext context = new VelocityContext();
            context.put("DATA_MAP", inputs);

            StringWriter writer = new StringWriter();
            t.merge(context, writer);
            requestJson = writer.toString();

            response = orderPreview(accountIdKey, requestJson);
        } catch (Exception e) {
            throw new ApiException("Error during order preview", e);
        }
        return response;
    }

    private String orderPreview(final String accountIdKey, final String request) throws ApiException {
        Message message = new Message();
        message.setOauthRequired(OauthRequired.YES);
        message.setHttpMethod("POST");
        message.setUrl(String.format("%s%s/orders/preview", apiResource.getApiBaseUrl(), accountIdKey));
        message.setContentType(ContentType.APPLICATION_JSON);
        message.setBody(request);
        return oauthManager.invoke(message);
    }

    public Map<String, Object> parseResponse(final String body) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
        JSONObject orderResponse = (JSONObject) jsonObject.get("PreviewOrderResponse");

        if (orderResponse != null) {
            JSONObject order = (JSONObject) ((JSONArray) orderResponse.get("Order")).get(0);
            JSONObject instrument = (JSONObject) ((JSONArray) order.get("Instrument")).get(0);
            JSONObject product = (JSONObject) instrument.get("Product");
            JSONObject previewId = (JSONObject) ((JSONArray) orderResponse.get("PreviewIds")).get(0);

            responseMap.put("PreviewId", previewId.get("previewId"));
            responseMap.put("AccountId", orderResponse.get("accountId"));
            responseMap.put("Symbol", product.get("symbol"));
            responseMap.put("TotalOrderValue", orderResponse.get("totalOrderValue"));
            responseMap.put("OrderTerm",
                    OrderUtil.getTerm(OrderTerm.getOrderTerm(String.valueOf(order.get("orderTerm")))));
            responseMap.put("PriceType",
                    OrderUtil.getPrice(PriceType.getPriceType(String.valueOf(order.get("priceType"))), order));
            responseMap.put("Commission", order.get("estimatedCommission"));
            responseMap.put("Description", instrument.get("symbolDescription"));
            responseMap.put("OrderAction", instrument.get("orderAction"));
            responseMap.put("Quantity", instrument.get("quantity"));
        }

        return responseMap;
    }

    public void fillOrderActionMenu(int choice, Map<String, String> input) {
        switch (choice) {
            case 1:
                input.put("ACTION", "BUY");
                break;
            case 2:
                input.put("ACTION", "SELL");
                break;
            case 3:
                input.put("ACTION", "SELL_SHORT");
                break;
        }
    }

    public void fillOrderPriceMenu(int choice, Map<String, String> input) {
        switch (choice) {
            case 1:
                input.put("PRICE_TYPE", "MARKET");
                break;
            case 2:
                input.put("PRICE_TYPE", "LIMIT");
                break;
        }
    }

    public void fillDurationMenu(int choice, Map<String, String> input) {
        switch (choice) {
            case 1:
                input.put("ORDER_TERM", "GOOD_FOR_DAY");
                break;
            case 2:
                input.put("ORDER_TERM", "IMMEDIATE_OR_CANCEL");
                break;
            case 3:
                input.put("ORDER_TERM", "FILL_OR_KILL");
                break;
        }
    }
}
