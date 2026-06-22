package com.aichatbot.customerservices.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import com.aichatbot.customerservices.config.JwtProperties;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

class JwtStompChannelInterceptorTest {

    @Test
    void connectFrameAuthenticatesUserFromBearerToken() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("change-me-change-me-change-me-32");
        properties.setIssuer("customer-service-backend");

        JwtTokenService tokenService = new JjwtJwtTokenService(properties);
        String token = tokenService.createToken("admin", List.of("ADMIN"));

        JwtStompChannelInterceptor interceptor = new JwtStompChannelInterceptor(tokenService);
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.addNativeHeader("Authorization", "Bearer " + token);

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        Message<?> processed = interceptor.preSend(message, null);

        assertNotNull(processed);
        StompHeaderAccessor processedAccessor = StompHeaderAccessor.wrap(processed);
        assertNotNull(processedAccessor.getUser());
        assertEquals("admin", processedAccessor.getUser().getName());
    }
}
