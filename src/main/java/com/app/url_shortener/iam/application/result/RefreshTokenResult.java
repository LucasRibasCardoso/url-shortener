package com.app.url_shortener.iam.application.result;

public record RefreshTokenResult(String newRefreshToken, String newAccessToken) {
}
