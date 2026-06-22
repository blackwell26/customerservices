package com.aichatbot.customerservices.service.mongo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.aichatbot.customerservices.service.ChatMessageRecord;
import com.aichatbot.customerservices.service.ChatSessionRecord;
import com.aichatbot.customerservices.service.ChatSessionStore;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.chat.storage", name = "mode", havingValue = "mongo")
public class MongoChatSessionStore implements ChatSessionStore {

    private final ChatSessionRepository repository;

    public MongoChatSessionStore(ChatSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public ChatSessionRecord loadOrCreate(String username, String sessionId) {
        String resolvedSessionId = resolveSessionId(sessionId);
        return repository.findByUsernameAndSessionId(username, resolvedSessionId)
                .map(this::toRecord)
                .orElseGet(() -> {
                    ChatSessionDocument created = new ChatSessionDocument();
                    created.setId(compositeId(username, resolvedSessionId));
                    created.setSessionId(resolvedSessionId);
                    created.setUsername(username);
                    created.setMessages(new ArrayList<>());
                    created.setCreatedAt(Instant.now());
                    created.setUpdatedAt(Instant.now());
                    return toRecord(repository.save(created));
                });
    }

    @Override
    public void save(ChatSessionRecord session) {
        ChatSessionDocument document = repository.findByUsernameAndSessionId(session.username(), session.sessionId())
                .orElseGet(ChatSessionDocument::new);
        document.setId(compositeId(session.username(), session.sessionId()));
        document.setSessionId(session.sessionId());
        document.setUsername(session.username());
        document.setMessages(session.messages().stream()
                .map(message -> new ChatMessageDocument(message.role(), message.text(), message.timestamp()))
                .toList());
        document.setCreatedAt(document.getCreatedAt() == null ? Instant.now() : document.getCreatedAt());
        document.setUpdatedAt(Instant.now());
        repository.save(document);
    }

    private ChatSessionRecord toRecord(ChatSessionDocument document) {
        List<ChatMessageRecord> messages = document.getMessages() == null
                ? List.of()
                : document.getMessages().stream()
                        .map(message -> new ChatMessageRecord(message.getRole(), message.getText(), message.getTimestamp()))
                        .toList();

        return new ChatSessionRecord(
                document.getSessionId(),
                document.getUsername(),
                messages,
                document.getCreatedAt(),
                document.getUpdatedAt());
    }

    private String resolveSessionId(String sessionId) {
        return sessionId == null || sessionId.isBlank() ? UUID.randomUUID().toString() : sessionId;
    }

    private String compositeId(String username, String sessionId) {
        return username + ":" + sessionId;
    }
}
