package com.app.url_shortener.auth.infrastructure.entity;

import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "password_reset_tokens")
public class PasswordResetTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(name = "token_hash", nullable = false, unique = true, columnDefinition = "TEXT")
  private String tokenHash;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "used_at")
  private Instant usedAt;

  public static PasswordResetTokenEntity issue(UserEntity user, String tokenHash, Instant expiresAt) {
    PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
    resetToken.user = requireNonNull(user, "user");
    resetToken.tokenHash = validateRequiredValue(tokenHash, "tokenHash");
    resetToken.expiresAt = validateFutureInstant(expiresAt, "expiresAt");
    return resetToken;
  }

  public void markAsUsed() {
    if (usedAt == null) {
      usedAt = Instant.now();
    }
  }

  public boolean isUsed() {
    return usedAt != null;
  }

  public boolean isExpired() {
    return !expiresAt.isAfter(Instant.now());
  }

  public boolean isActive() {
    return !isUsed() && !isExpired();
  }

  private static String validateRequiredValue(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be null or blank");
    }
    return value.trim();
  }

  private static Instant validateFutureInstant(Instant value, String fieldName) {
    Instant validatedValue = requireNonNull(value, fieldName);
    if (!validatedValue.isAfter(Instant.now())) {
      throw new IllegalArgumentException(fieldName + " must be in the future");
    }
    return validatedValue;
  }

  private static <T> T requireNonNull(T value, String fieldName) {
    if (value == null) {
      throw new IllegalArgumentException(fieldName + " must not be null");
    }
    return value;
  }
}
