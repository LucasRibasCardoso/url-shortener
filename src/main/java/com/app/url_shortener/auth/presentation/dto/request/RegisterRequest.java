package com.app.url_shortener.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 120) String name,
    @NotBlank @Email @Size(max = 180) String email,
    @NotBlank @Size(min = 12, max = 128) String password) {}
