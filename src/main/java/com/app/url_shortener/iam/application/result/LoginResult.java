package com.app.url_shortener.iam.application.result;

public record LoginResult(
        String refreshToken,
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        AuthenticatedUserResult user
) {
}
