package com.app.url_shortener.iam.domain.model;

import com.app.url_shortener.iam.domain.exception.auth.RefreshTokenExpiredException;
import com.app.url_shortener.iam.domain.exception.auth.TokenCompromisedException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RefreshToken {

  @EqualsAndHashCode.Include
  private final UUID id;

  private final UUID userId;

  private final String tokenHash;
  private final Instant createdAt;
  private final Instant expiresAt;

  private Instant revokedAt;
  private UUID replacedByTokenId;

  private RefreshToken(
          UUID id,
          UUID userId,
          String tokenHash,
          Instant createdAt,
          Instant expiresAt,
          Instant revokedAt,
          UUID replacedByTokenId) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.userId = Objects.requireNonNull(userId, "userId is required");
    this.tokenHash = validateTokenHash(tokenHash);
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt is required");
    this.revokedAt = revokedAt;
    this.replacedByTokenId = replacedByTokenId;
  }

  public static RefreshToken create(UUID userId, String tokenHash) {
    return new RefreshToken(
            UUID.randomUUID(),
            userId,
            tokenHash,
            Instant.now(),
            Instant.now().plus(7, ChronoUnit.DAYS),
            null,
            null
    );
  }

  public static RefreshToken restore(
          UUID id,
          UUID userId,
          String tokenHash,
          Instant createdAt,
          Instant expiresAt,
          Instant revokedAt,
          UUID replacedByTokenId) {
    return new RefreshToken(id, userId, tokenHash, createdAt, expiresAt, revokedAt, replacedByTokenId);
  }

  private static String validateTokenHash(String tokenHash) {
    if (tokenHash == null || tokenHash.isBlank()) {
      throw new IllegalArgumentException("tokenHash is required");
    }
    return tokenHash.trim();
  }

  // --- Comportamentos de Negócio (Rich Domain) ---

  public boolean isExpired(Instant now) {
    return now.isAfter(this.expiresAt);
  }

  public boolean isRevoked() {
    return this.revokedAt != null;
  }

  public void revoke() {
    if (!isRevoked()) {
      this.revokedAt = Instant.now();
    }
  }

  public RefreshToken rotate(String newTokenHash) {
    if (this.isRevoked()) {
      throw new TokenCompromisedException();
    }

    if (this.isExpired(Instant.now())) {
      throw new RefreshTokenExpiredException();
    }

    this.revoke();
    RefreshToken nextToken = RefreshToken.create(this.userId, newTokenHash);
    this.replacedByTokenId = nextToken.getId();

    return nextToken;
  }
}