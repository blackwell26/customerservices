package com.aichatbot.customerservices.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.aichatbot.customerservices.config.JwtProperties;

import org.junit.jupiter.api.Test;

class JjwtJwtTokenServiceTest {

    @Test
    void tokenRoundTripPreservesSubjectAndRoles() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("change-me-change-me-change-me-32");
        properties.setIssuer("customer-service-backend");
        properties.setExpirationMinutes(60);

        JwtTokenService tokenService = new JjwtJwtTokenService(properties);
        String token = tokenService.createToken("alice", List.of("CUSTOMER"));

        assertTrue(tokenService.parseToken(token).isPresent());
        JwtTokenService.JwtPrincipal principal = tokenService.parseToken(token).orElseThrow();

        assertEquals("alice", principal.subject());
        assertEquals(List.of("CUSTOMER"), principal.roles());
    }
}

