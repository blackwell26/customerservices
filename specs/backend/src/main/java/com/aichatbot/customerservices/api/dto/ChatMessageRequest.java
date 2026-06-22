package com.aichatbot.customerservices.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank String message,
        String sessionId) {
}

