package com.app.url_shortener.auth.domain.model;

import com.app.url_shortener.auth.domain.exception.RoleNameRequiredException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Role {

  private final UUID id;
  private final String name;
  private final Set<Permission> permissions;

  private Role(UUID id, String name, Set<Permission> permissions) {
    this.id = validateId(id);
    this.name = validateName(name);
    this.permissions = normalizePermissions(permissions);
  }

  public static Role create(UUID id, String name, Set<Permission> permissions) {
    return new Role(id, name, permissions);
  }

  private static String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new RoleNameRequiredException();
    }

    return name.trim();
  }

  private static UUID validateId(UUID id) {
    return Objects.requireNonNull(id, "id is required");
  }

  private static Set<Permission> normalizePermissions(Set<Permission> permissions) {
    if (permissions == null) {
      return Set.of();
    }

    return Collections.unmodifiableSet(new HashSet<>(permissions));
  }
}
