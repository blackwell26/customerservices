package com.aichatbot.customerservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class ChatConversationServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void reusesConversationWhenSessionIdIsProvided() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        ChatConversationService service = new ChatConversationService(
                new FileChatSessionStore(objectMapper, tempDir.resolve("chat-sessions.json")));
        var auth = new UsernamePasswordAuthenticationToken("admin", "n/a");

        var first = service.handleMessage(auth, null, "Where is my order?");
        var second = service.handleMessage(auth, first.sessionId(), "And what about shipping?");

        assertNotNull(first.sessionId());
        assertEquals(first.sessionId(), second.sessionId());
        assertEquals(4, second.history().size());
    }
}
