package com.aichatbot.customerservices.service;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderNumber) {
        super(orderNumber == null
                ? "Order number is required"
                : "No order tracking record found for order number " + orderNumber);
    }
}
