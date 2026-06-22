package com.aichatbot.customerservices.service.mongo;

import java.time.Instant;

public class ChatMessageDocument {

    private String role;
    private String text;
    private Instant timestamp;

    public ChatMessageDocument() {
    }

    public ChatMessageDocument(String role, String text, Instant timestamp) {
        this.role = role;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

