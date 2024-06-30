package com.sforce.sforcetrading.etradeintegration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sforce.sforcetrading.etradeintegration.clients.order.OrderClient;
import com.sforce.sforcetrading.etradeintegration.clients.order.OrderDetail;
import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderClient orderClient;

    public List<OrderDetail> getOrders(String accountIdKey) throws ApiException {
        String response = orderClient.getOrders(accountIdKey);
        try {
            return orderClient.parseResponse(response);
        } catch (Exception e) {
            throw new ApiException(500, "Error parsing order response", e.getMessage());
        }
    }
}
