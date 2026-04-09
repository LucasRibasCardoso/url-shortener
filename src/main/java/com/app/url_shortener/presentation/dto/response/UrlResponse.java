package com.app.url_shortener.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Objeto de resposta contendo os detalhes da URL encurtada")
public record UrlResponse(
    @Schema(description = "A URL original que foi fornecida", example = "https://www.github.com")
        String originalUrl,
    @Schema(
            description = "A URL curta gerada pronta para uso",
            example = "http://localhost:8080/aB3x9")
        String shortUrl,
    @Schema(description = "Data e hora em que a URL foi encurtada", example = "2026-04-08T21:30:59")
        LocalDateTime createdAt) {}
