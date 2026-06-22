package com.aichatbot.customerservices.service.mongo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatSessionRepository extends MongoRepository<ChatSessionDocument, String> {

    Optional<ChatSessionDocument> findByUsernameAndSessionId(String username, String sessionId);
}

