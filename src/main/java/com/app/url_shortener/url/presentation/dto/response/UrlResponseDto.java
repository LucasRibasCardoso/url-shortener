package com.app.url_shortener.url.presentation.dto.response;

import java.time.LocalDateTime;

public record UrlResponseDto(String originalUrl, String shortUrl, LocalDateTime createdAt) {
}
