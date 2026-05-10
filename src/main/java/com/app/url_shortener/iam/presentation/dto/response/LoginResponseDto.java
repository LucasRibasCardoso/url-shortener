package com.app.url_shortener.iam.presentation.dto.response;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        AuthenticatedUserDto user
) {
}
