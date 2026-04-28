package com.app.url_shortener.auth.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(
    UUID id,
    UUID userId,
    Instant createdAt,
    Instant expiresAt,
    boolean revoked,
    Instant lastUsedAt) {}
