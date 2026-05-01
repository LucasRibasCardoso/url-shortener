package com.app.url_shortener.iam.domain.model;

import com.app.url_shortener.iam.domain.enums.PlanType;
import com.app.url_shortener.iam.domain.enums.UserStatus;
import com.app.url_shortener.iam.domain.exception.validation.EmailRequiredException;
import com.app.url_shortener.iam.domain.exception.validation.PasswordHashRequiredException;
import com.app.url_shortener.iam.domain.exception.user.UserAccountLockedException;
import com.app.url_shortener.iam.domain.exception.validation.UserNameRequiredException;
import com.app.url_shortener.iam.domain.exception.user.UserAccountDisabledException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserAccount {

  @EqualsAndHashCode.Include
  private final UUID id;
  private final String name;
  private final String email;
  private final String passwordHash;
  private UserStatus status;
  private final PlanType plan;
  private boolean emailVerified;
  private int tokenVersion;
  private final Set<Role> roles;

  private UserAccount(
          UUID id,
          String name,
          String email,
          String passwordHash,
          UserStatus status,
          PlanType planType,
          boolean emailVerified,
          int tokenVersion,
          Set<Role> roles) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.name = validateName(name);
    this.email = validateEmail(email);
    this.passwordHash = validatePasswordHash(passwordHash);
    this.status = status;
    this.plan = planType;
    this.emailVerified = emailVerified;
    this.tokenVersion = tokenVersion;
    this.roles = roles == null ? new HashSet<>() : new HashSet<>(roles);
  }

  public static UserAccount createPendingRegistration(
          String name,
          String email,
          String passwordHash) {

    UUID id = UUID.randomUUID();
    UserStatus status = UserStatus.PENDING_EMAIL_VERIFICATION;
    PlanType planType = PlanType.FREE;
    return new UserAccount(id, name, email, passwordHash, status, planType, false, 0, null);
  }

  public static UserAccount restore(
          UUID id,
          String name,
          String email,
          String passwordHash,
          UserStatus status,
          PlanType planType,
          boolean emailVerified,
          int tokenVersion,
          Set<Role> roles) {
    return new UserAccount(id, name, email, passwordHash, status, planType, emailVerified, tokenVersion, roles);
  }

  public Set<Role> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

  private static String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new UserNameRequiredException();
    }

    return name.trim();
  }

  private static String validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new EmailRequiredException();
    }

    return email.trim();
  }

  private static String validatePasswordHash(String passwordHash) {
    if (passwordHash == null || passwordHash.isBlank()) {
      throw new PasswordHashRequiredException();
    }

    return passwordHash.trim();
  }

  public void verifyEmail(Role defaultRole) {
    Objects.requireNonNull(defaultRole, "defaultRole must not be null");

    if (this.emailVerified && this.status == UserStatus.ACTIVE) {
      return;
    }

    if (this.status == UserStatus.LOCKED) {
      throw new UserAccountLockedException();
    }

    if (this.status == UserStatus.DISABLED) {
      throw new UserAccountDisabledException();
    }

    this.emailVerified = true;
    this.status = UserStatus.ACTIVE;
    this.roles.add(defaultRole);
  }

  public void revokeAllSessions() {
    this.tokenVersion++;
  }
}
