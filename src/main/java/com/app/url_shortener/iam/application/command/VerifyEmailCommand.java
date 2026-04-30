package com.app.url_shortener.iam.application.command;

import com.app.url_shortener.iam.domain.valueobject.VerificationCode;

public record VerifyEmailCommand(
        String email,
        VerificationCode code) {
}
