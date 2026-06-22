package com.aichatbot.customerservices.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    private String issuer = "customer-service-backend";
    private String secret = "change-me-change-me-change-me-32";
    private long expirationMinutes = 60;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
}

