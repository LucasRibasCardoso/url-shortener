package com.app.url_shortener.auth.infrastructure.entity;

import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(name = "token_hash", nullable = false, unique = true, columnDefinition = "TEXT")
  private String tokenHash;

  @Column(name = "user_agent", columnDefinition = "TEXT")
  private String userAgent;

  @Column(name = "ip_address", columnDefinition = "inet")
  private InetAddress ipAddress;

  @Column(name = "device_name", length = 120)
  private String deviceName;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "replaced_by_token_id")
  private RefreshTokenEntity replacedByToken;

  public static RefreshTokenEntity issue(
          UserEntity user,
          String tokenHash,
          Instant expiresAt,
          String userAgent,
          InetAddress ipAddress,
          String deviceName) {
    RefreshTokenEntity refreshToken = new RefreshTokenEntity();
    refreshToken.user = requireNonNull(user, "user");
    refreshToken.tokenHash = validateRequiredValue(tokenHash, "tokenHash");
    refreshToken.expiresAt = validateFutureInstant(expiresAt, "expiresAt");
    refreshToken.userAgent = normalizeOptionalText(userAgent);
    refreshToken.ipAddress = ipAddress;
    refreshToken.deviceName = normalizeOptionalText(deviceName);
    return refreshToken;
  }

  public void revoke() {
    if (revokedAt == null) {
      revokedAt = Instant.now();
    }
  }

  public void replaceBy(RefreshTokenEntity newToken) {
    RefreshTokenEntity replacement = requireNonNull(newToken, "newToken");
    if (this == replacement) {
      throw new IllegalArgumentException("newToken must not be the same token");
    }

    revoke();
    replacedByToken = replacement;
  }

  public boolean isRevoked() {
    return revokedAt != null;
  }

  public boolean isExpired() {
    return !expiresAt.isAfter(Instant.now());
  }

  public boolean isActive() {
    return !isRevoked() && !isExpired();
  }

  private static String validateRequiredValue(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be null or blank");
    }
    return value.trim();
  }

  private static String normalizeOptionalText(String value) {
    if (value == null) {
      return null;
    }

    String normalizedValue = value.trim();
    return normalizedValue.isBlank() ? null : normalizedValue;
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
