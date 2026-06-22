package com.aichatbot.customerservices.api;

import com.aichatbot.customerservices.api.dto.OrderTrackingResponse;
import com.aichatbot.customerservices.service.OrderTrackingService;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/orders")
public class OrderTrackingController {

    private final OrderTrackingService orderTrackingService;

    public OrderTrackingController(OrderTrackingService orderTrackingService) {
        this.orderTrackingService = orderTrackingService;
    }

    @GetMapping("/track/{number}")
    public OrderTrackingResponse trackOrder(@PathVariable("number") @NotBlank String orderNumber) {
        return orderTrackingService.track(orderNumber);
    }
}
