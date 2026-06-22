package com.aichatbot.customerservices.security;

import java.util.List;
import java.util.Optional;

public interface JwtTokenService {

    String createToken(String subject, List<String> roles);

    Optional<JwtPrincipal> parseToken(String token);

    record JwtPrincipal(String subject, List<String> roles) {
    }
}

