package com.app.url_shortener.url.presentation.dto.request;

import com.app.url_shortener.url.presentation.validator.ValidHttpUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto de requisição contendo a URL original a ser encurtada")
public record ShortenUrlRequest(
    @Schema(
            description = "A URL original completa (deve incluir http:// ou https://)",
            example = "https://www.github.com")
        @NotBlank(message = "A URL é obrigatória")
        @ValidHttpUrl
        String originalUrl) {}
