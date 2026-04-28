package com.app.url_shortener.auth.presentation.dto.response;

public record TokenResponse(String accessToken, String tokenType, Long expiresIn) {
}
