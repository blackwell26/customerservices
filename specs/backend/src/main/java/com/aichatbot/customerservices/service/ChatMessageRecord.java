package com.aichatbot.customerservices.service;

import java.time.Instant;

public record ChatMessageRecord(String role, String text, Instant timestamp) {
}

