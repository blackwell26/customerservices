package com.aichatbot.customerservices.api;

import com.aichatbot.customerservices.api.dto.ChatMessageRequest;
import com.aichatbot.customerservices.api.dto.ChatMessageResponse;
import com.aichatbot.customerservices.service.ChatConversationService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatConversationService chatConversationService;

    public ChatController(ChatConversationService chatConversationService) {
        this.chatConversationService = chatConversationService;
    }

    @PostMapping("/messages")
    public ChatMessageResponse sendMessage(Authentication authentication, @Valid @RequestBody ChatMessageRequest request) {
        var result = chatConversationService.handleMessage(authentication, request.sessionId(), request.message());
        return new ChatMessageResponse(result.sessionId(), result.userMessage(), result.assistantMessage(), result.history());
    }
}

