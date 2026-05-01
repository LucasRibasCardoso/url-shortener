package com.app.url_shortener.shared.infrastructure.idempotency;

public record CachedResponse(int status, byte[] body) {
}
