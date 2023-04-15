package com.upgrade.orderprocessingservice.controller;

import com.upgrade.orderprocessingservice.entity.Order;
import com.upgrade.orderprocessingservice.exceptions.OrderProcessingFailedException;
import com.upgrade.orderprocessingservice.model.OrderResponseVO;
import com.upgrade.orderprocessingservice.model.OrderVO;
import com.upgrade.orderprocessingservice.service.OrderProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderProcessingController {

    OrderProcessingService service;

    public OrderProcessingController(OrderProcessingService service) {
        this.service = service;
    }

    @PostMapping("v1/order")
    public ResponseEntity<OrderResponseVO> createOrder(@RequestBody OrderVO order) throws OrderProcessingFailedException {

        return ResponseEntity.ok(service.createOrder(order));
    }
}
