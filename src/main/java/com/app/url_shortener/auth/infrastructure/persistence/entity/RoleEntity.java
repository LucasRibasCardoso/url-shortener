package com.app.url_shortener.auth.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "roles")
public class RoleEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id = UUID.randomUUID();

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(name = "is_default", nullable = false)
  private boolean isDefault;
  
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Getter(AccessLevel.NONE)
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "role_permissions",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  private Set<PermissionEntity> permissions = new HashSet<>();

  public static RoleEntity create(String name) {
    RoleEntity role = new RoleEntity();
    role.name = validateRequiredValue(name, "name");
    return role;
  }

  public void changeName(String name) {
    this.name = validateRequiredValue(name, "name");
  }

  public void addPermission(PermissionEntity permission) {
    permissions.add(requirePermission(permission));
  }

  public void removePermission(PermissionEntity permission) {
    permissions.remove(requirePermission(permission));
  }

  public Set<PermissionEntity> getPermissions() {
    return Collections.unmodifiableSet(permissions);
  }

  private static String validateRequiredValue(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be null or blank");
    }
    return value.trim();
  }

  private static PermissionEntity requirePermission(PermissionEntity permission) {
    if (permission == null) {
      throw new IllegalArgumentException("permission must not be null");
    }
    return permission;
  }
}
