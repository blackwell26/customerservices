package com.aichatbot.customerservices.api;

import java.util.List;

public record AccountResponse(String username, List<String> roles) {
}

