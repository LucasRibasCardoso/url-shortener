package com.app.url_shortener.user.infrastructure.entity;

import com.app.url_shortener.auth.infrastructure.persistence.entity.RoleEntity;
import com.app.url_shortener.user.domain.enums.PlanType;
import com.app.url_shortener.user.domain.enums.UserStatus;
import com.app.url_shortener.user.domain.exception.InvalidUserPlanException;
import com.app.url_shortener.user.domain.exception.UserAccountBlockedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class UserEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id = UUID.randomUUID();

  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, unique = true, columnDefinition = "citext")
  private String email;

  @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PlanType plan = PlanType.FREE;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private UserStatus status = UserStatus.PENDING_EMAIL_VERIFICATION;

  @Column(name = "token_version", nullable = false)
  private int tokenVersion = 0;

  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified = false;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  private UserEntity createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by")
  private UserEntity updatedBy;

  @Getter(AccessLevel.NONE)
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles = new HashSet<>();

  public static UserEntity createPendingRegistration(String name, String email, String passwordHash) {
    UserEntity user = new UserEntity();
    user.name = validateRequiredValue(name, "name");
    user.email = validateRequiredValue(email, "email");
    user.passwordHash = validateRequiredValue(passwordHash, "passwordHash");
    user.plan = PlanType.FREE;
    user.status = UserStatus.PENDING_EMAIL_VERIFICATION;
    user.emailVerified = false;
    user.tokenVersion = 0;
    return user;
  }

  public void changePlan(PlanType newPlan) {
    plan = validatePersistedPlan(newPlan);
  }

  public void addRole(RoleEntity role) {
    roles.add(requireRole(role));
  }

  public void changeName(String name) {
    this.name = validateRequiredValue(name, "name");
  }

  public Set<RoleEntity> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

  private static String validateRequiredValue(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be null or blank");
    }
    return value.trim();
  }

  private static RoleEntity requireRole(RoleEntity role) {
    if (role == null) {
      throw new IllegalArgumentException("role must not be null");
    }
    return role;
  }

  private static PlanType validatePersistedPlan(PlanType plan) {
    if (plan == null || plan == PlanType.ANONYMOUS) {
      throw new InvalidUserPlanException();
    }
    return plan;
  }

  public void verifyEmail(RoleEntity userRole) {
    Objects.requireNonNull(userRole, "userRole must not be null");

    if (this.emailVerified && this.status == UserStatus.ACTIVE) {
      return;
    }

    if (this.status == UserStatus.BLOCKED) {
      throw new UserAccountBlockedException();
    }

    this.emailVerified = true;
    this.status = UserStatus.ACTIVE;
    this.addRole(userRole);
  }
}
