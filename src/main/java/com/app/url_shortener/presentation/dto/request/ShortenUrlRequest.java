package com.app.url_shortener.presentation.dto.request;

import com.app.url_shortener.presentation.validator.ValidHttpUrl;
import jakarta.validation.constraints.NotBlank;

public record ShortenUrlRequest(
    @NotBlank(message = "A URL é obrigatória") @ValidHttpUrl String originalUrl) {}
