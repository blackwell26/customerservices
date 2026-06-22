package com.aichatbot.customerservices.api;

import java.time.Instant;

import com.aichatbot.customerservices.service.KnowledgeDocumentValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = KnowledgeBaseController.class)
public class KnowledgeBaseExceptionHandler {

    @ExceptionHandler(KnowledgeDocumentValidationException.class)
    public ResponseEntity<KnowledgeErrorResponse> handleValidation(KnowledgeDocumentValidationException ex) {
        return ResponseEntity.status(ex.status())
                .body(new KnowledgeErrorResponse(ex.code(), ex.getMessage(), Instant.now().toString()));
    }

    public record KnowledgeErrorResponse(String code, String message, String timestamp) {
    }
}
