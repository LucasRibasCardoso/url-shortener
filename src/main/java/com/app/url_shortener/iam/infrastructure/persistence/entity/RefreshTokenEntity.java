package com.app.url_shortener.iam.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;

@Getter
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

  public RefreshTokenEntity() {
  }

  public RefreshTokenEntity(
          UUID id,
          UserEntity user,
          String tokenHash,
          String userAgent,
          InetAddress ipAddress,
          String deviceName,
          Instant createdAt,
          Instant expiresAt,
          Instant revokedAt,
          RefreshTokenEntity replacedByToken) {
    this.id = id;
    this.user = user;
    this.tokenHash = tokenHash;
    this.userAgent = userAgent;
    this.ipAddress = ipAddress;
    this.deviceName = deviceName;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
    this.revokedAt = revokedAt;
    this.replacedByToken = replacedByToken;
  }
}
