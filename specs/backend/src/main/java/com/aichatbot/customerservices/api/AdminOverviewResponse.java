package com.aichatbot.customerservices.api;

import java.util.List;

public record AdminOverviewResponse(String requiredRole, List<String> supportedRoles, String message) {
}

