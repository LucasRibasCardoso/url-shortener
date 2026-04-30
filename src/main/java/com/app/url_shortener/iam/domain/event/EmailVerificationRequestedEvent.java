package com.app.url_shortener.iam.domain.event;

import com.app.url_shortener.iam.domain.valueobject.VerificationCode;
import java.util.UUID;

public record EmailVerificationRequestedEvent(
    UUID userId,
    String email,
    VerificationCode code) {
}
