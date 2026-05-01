package com.app.url_shortener.iam.application.result;

public record LoginResult(String accessToken, String tokenType, Long expiresInSeconds, AuthenticatedUserResult user) {
}
