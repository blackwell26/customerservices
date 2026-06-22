package com.aichatbot.customerservices.security;

import java.util.List;
import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.support.ChannelInterceptor;

public class JwtStompChannelInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenService jwtTokenService;

    public JwtStompChannelInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
            UsernamePasswordAuthenticationToken authentication = resolveAuthentication(authorization)
                    .orElseThrow(() -> new AccessDeniedException("Missing or invalid JWT token"));

            accessor.setUser(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        return message;
    }

    private Optional<UsernamePasswordAuthenticationToken> resolveAuthentication(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authorization.substring(7);
        return jwtTokenService.parseToken(token)
                .map(principal -> new UsernamePasswordAuthenticationToken(
                        principal.subject(),
                        token,
                        principal.roles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .toList()));
    }
}
