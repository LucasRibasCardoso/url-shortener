package com.app.url_shortener.auth.application.command;

import com.app.url_shortener.auth.domain.valueobject.VerificationCode;

public record VerifyEmailCommand(
        String email,
        VerificationCode code) {
}
