package com.sforce.sforcetrading.etradeintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sforce.sforcetrading.etradeintegration.service.OrderService;
import com.sforce.sforcetrading.etradeintegration.clients.order.OrderDetail;
import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import java.util.List;

@RestController
@RequestMapping("/api/etrade/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{accountIdKey}")
    public List<OrderDetail> getOrders(@PathVariable String accountIdKey) throws ApiException {
        return orderService.getOrders(accountIdKey);
    }
}
