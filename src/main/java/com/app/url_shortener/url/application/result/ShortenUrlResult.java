package com.app.url_shortener.url.application.result;

import java.time.LocalDateTime;

public record ShortenUrlResult(String originalUrl, String shortCode, LocalDateTime createdAt) {
}
