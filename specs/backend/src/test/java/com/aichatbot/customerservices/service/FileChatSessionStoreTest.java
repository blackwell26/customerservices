package com.aichatbot.customerservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileChatSessionStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void persistsConversationAcrossInstances() {
        Path storageFile = tempDir.resolve("chat-sessions.json");
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        FileChatSessionStore firstStore = new FileChatSessionStore(objectMapper, storageFile);
        ChatSessionRecord created = firstStore.loadOrCreate("admin", null).withMessage("user", "Where is my order?");
        firstStore.save(created);

        assertFalse(created.messages().isEmpty());
        assertTrue(Files.exists(storageFile));

        FileChatSessionStore secondStore = new FileChatSessionStore(objectMapper, storageFile);
        secondStore.load();
        ChatSessionRecord loaded = secondStore.loadOrCreate("admin", created.sessionId());

        assertEquals(created.sessionId(), loaded.sessionId());
        assertEquals(1, loaded.messages().size());
        assertEquals("Where is my order?", loaded.messages().get(0).text());
    }
}
