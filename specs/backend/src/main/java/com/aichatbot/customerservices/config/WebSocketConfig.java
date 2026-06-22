package com.aichatbot.customerservices.config;

import com.aichatbot.customerservices.security.JwtStompChannelInterceptor;
import com.aichatbot.customerservices.security.JwtTokenService;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenService jwtTokenService;
    private final WebSocketProperties webSocketProperties;

    public WebSocketConfig(JwtTokenService jwtTokenService, WebSocketProperties webSocketProperties) {
        this.jwtTokenService = jwtTokenService;
        this.webSocketProperties = webSocketProperties;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtStompChannelInterceptor(jwtTokenService));
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(webSocketProperties.getAllowedOrigins().toArray(String[]::new));
    }
}
