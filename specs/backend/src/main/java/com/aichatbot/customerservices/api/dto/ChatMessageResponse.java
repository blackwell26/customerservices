package com.aichatbot.customerservices.api.dto;

import java.util.List;

public record ChatMessageResponse(
        String sessionId,
        String userMessage,
        String assistantMessage,
        List<ChatTurn> history) {

    public record ChatTurn(String role, String text) {
    }
}

