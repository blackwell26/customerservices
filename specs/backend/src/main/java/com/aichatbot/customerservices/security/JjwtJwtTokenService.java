package com.aichatbot.customerservices.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import com.aichatbot.customerservices.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JjwtJwtTokenService implements JwtTokenService {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JjwtJwtTokenService(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getExpirationMinutes() * 60);

        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(subject)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Optional<JwtPrincipal> parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            List<String> safeRoles = roles == null ? List.of() : roles.stream().map(String::valueOf).collect(Collectors.toList());

            return Optional.of(new JwtPrincipal(claims.getSubject(), safeRoles));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
