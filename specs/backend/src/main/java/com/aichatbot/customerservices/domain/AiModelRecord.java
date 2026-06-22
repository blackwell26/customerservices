package com.aichatbot.customerservices.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record AiModelRecord(
        UUID modelId,
        String modelName,
        String modelVersion,
        AiModelProvider provider,
        String runtime,
        boolean active,
        String defaultUseCase,
        Instant createdAt,
        Instant updatedAt) {

    public AiModelRecord {
        Objects.requireNonNull(modelId, "modelId must not be null");
        Objects.requireNonNull(modelName, "modelName must not be null");
        Objects.requireNonNull(modelVersion, "modelVersion must not be null");
        Objects.requireNonNull(provider, "provider must not be null");
        Objects.requireNonNull(runtime, "runtime must not be null");
        Objects.requireNonNull(defaultUseCase, "defaultUseCase must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
