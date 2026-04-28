package com.app.url_shortener.auth.presentation.dto.response;

import com.app.url_shortener.user.presentation.dto.response.UserResponse;

public record AuthResponse(
    String accessToken,
    String tokenType,
    Long expiresIn,
    UserResponse user) {}
