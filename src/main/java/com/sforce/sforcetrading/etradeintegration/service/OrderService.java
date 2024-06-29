package com.sforce.sforcetrading.etradeintegration.service;

import com.sforce.sforcetrading.etradeintegration.clients.order.OrderClient;
import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private AccountService accountService;

    public String getOrders(String acctIndex) {
        String response = "";
        String accountIdKey = "";
        try {
            accountIdKey = accountService.getAccountIdKeyForIndex(acctIndex);
        } catch (ApiException e) {
            return "Invalid account index";
        }
        try {
            response = orderClient.getOrders(accountIdKey);
            log.debug(" Get Order response : " + response);
            if (response != null) {
                StringBuilder result = new StringBuilder();
                result.append("\t\t Orders for selected account index : ").append(acctIndex).append("\n\n");

                // Use OrderUtil methods for processing response data
                // Example: Parsing and formatting the response as needed
                // Assuming parseResponse method processes and stores the parsed data
                orderClient.parseResponse(response);

                // Add additional logic to format the output if needed
                // For example:
                // result.append(OrderUtil.getPrice(priceType, orderDetail));
                // result.append(OrderUtil.getTerm(orderTerm));
                // result.append(OrderUtil.convertLongToDate(ldate));

                return result.toString();
            } else {
                return "No records...";
            }
        } catch (ApiException e) {
            log.error("API Exception: HttpStatus: {}, Message: {}, Error Code: {}", e.getHttpStatus(), e.getMessage(),
                    e.getCode());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
        }
        return null;
    }
}
