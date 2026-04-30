package com.app.url_shortener.auth.domain.model;

import com.app.url_shortener.auth.domain.exception.EmailAlreadyVerifiedException;
import com.app.url_shortener.auth.domain.exception.EmailRequiredException;
import com.app.url_shortener.auth.domain.exception.PasswordHashRequiredException;
import com.app.url_shortener.auth.domain.exception.UserNameRequiredException;
import com.app.url_shortener.auth.infrastructure.persistence.entity.RoleEntity;
import com.app.url_shortener.user.domain.enums.UserStatus;
import com.app.url_shortener.user.domain.exception.UserAccountBlockedException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
@EqualsAndHashCode
public class AuthUser {

  private final UUID id;
  private final String name;
  private final String email;
  private final String passwordHash;
  private boolean emailVerified;
  private final Set<Role> roles;

  private AuthUser(
          UUID id,
          String name,
          String email,
          String passwordHash,
          boolean emailVerified,
          Set<Role> roles) {
    this.id = validateId(id);
    this.name = validateName(name);
    this.email = validateEmail(email);
    this.passwordHash = validatePasswordHash(passwordHash);
    this.emailVerified = emailVerified;
    this.roles = roles == null ? new HashSet<>() : new HashSet<>(roles);
  }

  public static AuthUser createPendingRegistration(
          String name,
          String email,
          String passwordHash) {
    return new AuthUser(UUID.randomUUID(), name, email, passwordHash, false, null);
  }

  public static AuthUser restore(
          UUID id,
          String name,
          String email,
          String passwordHash,
          boolean emailVerified,
          Set<Role> roles) {
    return new AuthUser(id, name, email, passwordHash, emailVerified, roles);
  }

  public Set<Role> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

  private static UUID validateId(UUID id) {
    return Objects.requireNonNull(id, "id is required");
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
}
