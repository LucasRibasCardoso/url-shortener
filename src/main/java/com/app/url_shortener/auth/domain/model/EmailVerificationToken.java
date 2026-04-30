package com.app.url_shortener.auth.domain.model;

import com.app.url_shortener.auth.domain.exception.EmailRequiredException;
import com.app.url_shortener.auth.domain.valueobject.VerificationCode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class EmailVerificationToken {

  private final UUID userId;
  private final String email;
  private final VerificationCode code;
  private final Instant expiresAt;

  private EmailVerificationToken(UUID userId, String email, VerificationCode code, Instant expiresAt) {
    this.userId = validateUserId(userId);
    this.email = validateEmail(email);
    this.code = validateCode(code);
    this.expiresAt = validateExpiresAt(expiresAt);
  }

  public static EmailVerificationToken create(
      UUID userId,
      String email,
      VerificationCode code,
      Instant expiresAt) {
    return new EmailVerificationToken(userId, email, code, expiresAt);
  }

  public boolean matches(VerificationCode code) {
    return this.code.equals(code);
  }

  public boolean isExpired() {
    Instant now = Instant.now();
    return !expiresAt.isAfter(now);
  }

  private static UUID validateUserId(UUID userId) {
    return Objects.requireNonNull(userId, "userId is required");
  }

  private static String validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new EmailRequiredException();
    }

    return email.trim();
  }

  private static VerificationCode validateCode(VerificationCode code) {
    return Objects.requireNonNull(code, "code is required");
  }

  private static Instant validateExpiresAt(Instant expiresAt) {
    return Objects.requireNonNull(expiresAt, "expiresAt is required");
  }
}
