package com.app.url_shortener.user.presentation.dto.response;

import com.app.url_shortener.user.domain.enums.PlanType;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        PlanType plan,
        Instant createdAt) {
}
