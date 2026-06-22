package com.aichatbot.customerservices.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.chat.storage", name = "mode", havingValue = "file", matchIfMissing = true)
public class FileChatSessionStore implements ChatSessionStore {

    private static final TypeReference<Map<String, ChatSessionRecord>> STORE_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storageFile;
    private final Map<String, ChatSessionRecord> sessions = new HashMap<>();

    @Autowired
    public FileChatSessionStore(
            ObjectMapper objectMapper,
            @Value("${app.chat.storage.file:}") String storageFilePath) {
        this(objectMapper, resolveStorageFile(storageFilePath));
    }

    public FileChatSessionStore(ObjectMapper objectMapper, Path storageFile) {
        this.objectMapper = objectMapper;
        this.storageFile = storageFile;
    }

    @PostConstruct
    void load() {
        synchronized (sessions) {
            if (Files.exists(storageFile)) {
                try {
                    Map<String, ChatSessionRecord> persisted = objectMapper.readValue(storageFile.toFile(), STORE_TYPE);
                    sessions.clear();
                    sessions.putAll(persisted);
                } catch (IOException ex) {
                    throw new IllegalStateException("Failed to load chat session store", ex);
                }
            }
        }
    }

    @Override
    public ChatSessionRecord loadOrCreate(String username, String sessionId) {
        synchronized (sessions) {
            String resolvedSessionId = resolveSessionId(sessionId);
            String key = conversationKey(username, resolvedSessionId);
            ChatSessionRecord existing = sessions.get(key);
            if (existing != null) {
                return existing;
            }

            ChatSessionRecord created = new ChatSessionRecord(
                    resolvedSessionId,
                    username,
                    List.of(),
                    Instant.now(),
                    Instant.now());
            sessions.put(key, created);
            persist();
            return created;
        }
    }

    @Override
    public void save(ChatSessionRecord session) {
        synchronized (sessions) {
            sessions.put(conversationKey(session.username(), session.sessionId()), session);
            persist();
        }
    }

    private void persist() {
        try {
            Path parent = storageFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile.toFile(), sessions);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to persist chat session store", ex);
        }
    }

    private String conversationKey(String username, String sessionId) {
        return username + ":" + sessionId;
    }

    private String resolveSessionId(String sessionId) {
        return sessionId == null || sessionId.isBlank() ? UUID.randomUUID().toString() : sessionId;
    }

    private static Path resolveStorageFile(String storageFilePath) {
        if (storageFilePath != null && !storageFilePath.isBlank()) {
            return Path.of(storageFilePath);
        }

        return Path.of(System.getProperty("java.io.tmpdir"), "customer-service-chat", "chat-sessions.json");
    }
}
