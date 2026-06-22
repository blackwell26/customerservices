package com.aichatbot.customerservices.api;

import java.time.Instant;

import com.aichatbot.customerservices.service.OrderNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = OrderTrackingController.class)
public class OrderTrackingExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        "ORDER_NOT_FOUND",
                        ex.getMessage(),
                        Instant.now().toString()));
    }

    public record ApiErrorResponse(String code, String message, String timestamp) {
    }
}
