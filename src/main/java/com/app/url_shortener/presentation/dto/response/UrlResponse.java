package com.app.url_shortener.presentation.dto.response;

import java.time.LocalDateTime;

public record UrlResponse(String originalUrl, String shortUrl, LocalDateTime createdAt) {


}

