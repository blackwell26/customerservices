package com.aichatbot.customerservices.service.mongo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.aichatbot.customerservices.service.ChatMessageRecord;
import com.aichatbot.customerservices.service.ChatSessionRecord;

import org.junit.jupiter.api.Test;

class MongoChatSessionStoreTest {

    @Test
    void loadsExistingConversationFromRepository() {
        ChatSessionRepository repository = mock(ChatSessionRepository.class);
        ChatSessionDocument document = new ChatSessionDocument();
        document.setId("admin:session-1");
        document.setSessionId("session-1");
        document.setUsername("admin");
        document.setMessages(List.of(new ChatMessageDocument("user", "Hello", Instant.parse("2026-06-22T00:00:00Z"))));
        document.setCreatedAt(Instant.parse("2026-06-22T00:00:00Z"));
        document.setUpdatedAt(Instant.parse("2026-06-22T00:01:00Z"));

        when(repository.findByUsernameAndSessionId("admin", "session-1")).thenReturn(Optional.of(document));

        MongoChatSessionStore store = new MongoChatSessionStore(repository);
        ChatSessionRecord session = store.loadOrCreate("admin", "session-1");

        assertEquals("session-1", session.sessionId());
        assertEquals("admin", session.username());
        assertEquals(1, session.messages().size());
        assertEquals("Hello", session.messages().get(0).text());
    }
}
