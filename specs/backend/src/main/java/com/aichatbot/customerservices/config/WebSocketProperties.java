package com.aichatbot.customerservices.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.websocket")
public class WebSocketProperties {

    private List<String> allowedOrigins = new ArrayList<>();

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}

