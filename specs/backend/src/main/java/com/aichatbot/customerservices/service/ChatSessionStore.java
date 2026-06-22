package com.aichatbot.customerservices.service;

public interface ChatSessionStore {

    ChatSessionRecord loadOrCreate(String username, String sessionId);

    void save(ChatSessionRecord session);
}

