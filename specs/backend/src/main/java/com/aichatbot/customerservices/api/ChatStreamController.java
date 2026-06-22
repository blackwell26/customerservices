package com.aichatbot.customerservices.api;

import com.aichatbot.customerservices.api.dto.ChatMessageRequest;
import com.aichatbot.customerservices.api.dto.ChatMessageResponse;
import com.aichatbot.customerservices.service.ChatConversationService;

import jakarta.validation.Valid;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class ChatStreamController {

    private final ChatConversationService chatConversationService;

    public ChatStreamController(ChatConversationService chatConversationService) {
        this.chatConversationService = chatConversationService;
    }

    @MessageMapping("/chat")
    @SendToUser("/queue/reply")
    public ChatMessageResponse streamMessage(Principal principal, @Valid @Payload ChatMessageRequest request) {
        var result = chatConversationService.handleMessage(principal, request.sessionId(), request.message());
        return new ChatMessageResponse(result.sessionId(), result.userMessage(), result.assistantMessage(), result.history());
    }
}
