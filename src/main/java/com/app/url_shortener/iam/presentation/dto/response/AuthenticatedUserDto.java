package com.app.url_shortener.iam.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUserDto(
        UUID id,
        String name,
        String email,
        String plan,
        List<String> roles
) {
}
