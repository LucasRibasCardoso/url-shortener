package com.app.url_shortener.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.idempotency")
public record IdempotencyProperties(List<String> protectedUris) {
}
