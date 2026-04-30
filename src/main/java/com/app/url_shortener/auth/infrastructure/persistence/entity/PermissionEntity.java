package com.app.url_shortener.auth.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "permissions")
public class PermissionEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id = UUID.randomUUID();

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  private String description;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public static PermissionEntity create(String name, String description) {
    PermissionEntity permission = new PermissionEntity();
    permission.name = validateRequiredValue(name, "name");
    permission.description = normalizeOptionalText(description);
    return permission;
  }

  public void changeName(String name) {
    this.name = validateRequiredValue(name, "name");
  }

  public void changeDescription(String description) {
    this.description = normalizeOptionalText(description);
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
}
