package com.aichatbot.customerservices.service;

import java.util.List;
import java.security.Principal;

import com.aichatbot.customerservices.api.dto.ChatMessageResponse.ChatTurn;

import org.springframework.stereotype.Service;

@Service
public class ChatConversationService {

    private final ChatSessionStore sessionStore;

    public ChatConversationService(ChatSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public ChatConversationResult handleMessage(Principal principal, String incomingSessionId, String message) {
        ChatSessionRecord session = sessionStore.loadOrCreate(principal.getName(), incomingSessionId);
        session = session.withMessage("user", message);
        String assistantMessage = generateReply(message);
        session = session.withMessage("assistant", assistantMessage);
        sessionStore.save(session);

        return new ChatConversationResult(
                session.sessionId(),
                message,
                assistantMessage,
                session.messages().stream()
                        .map(messageRecord -> new ChatTurn(messageRecord.role(), messageRecord.text()))
                        .toList());
    }

    private String generateReply(String message) {
        String normalized = message.toLowerCase();
        if (normalized.contains("order")) {
            return "I can help with order status. Please share your order number or tracking ID.";
        }
        if (normalized.contains("refund") || normalized.contains("return")) {
            return "I can help with refunds and returns. I will check eligibility and the relevant policy.";
        }
        if (normalized.contains("shipping")) {
            return "I can help with shipping details. Please provide the tracking number if you have it.";
        }
        if (normalized.contains("product")) {
            return "I can help with product details, pricing, and availability.";
        }
        return "I'm here to help. Please share more details so I can answer accurately.";
    }

    public record ChatConversationResult(
            String sessionId,
            String userMessage,
            String assistantMessage,
            List<ChatTurn> history) {
    }
}
