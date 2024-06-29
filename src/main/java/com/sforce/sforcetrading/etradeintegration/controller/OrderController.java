package com.sforce.sforcetrading.etradeintegration.controller;

import com.sforce.sforcetrading.etradeintegration.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String getOrders(@RequestParam String accountIndex) {
        return orderService.getOrders(accountIndex);
    }
}
