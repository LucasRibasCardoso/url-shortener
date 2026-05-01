package com.app.url_shortener.iam.domain.model;

import com.app.url_shortener.iam.domain.exception.rbac.RoleNameRequiredException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

  @EqualsAndHashCode.Include
  private final UUID id;
  private final String name;
  private final boolean isDefault;
  private final Set<Permission> permissions;

  private Role(UUID id, String name, boolean isDefault, Set<Permission> permissions) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.name = validateName(name);
    this.isDefault = isDefault;
    this.permissions = permissions == null ? new HashSet<>() : permissions;
  }

  public static Role create(String name, Set<Permission> permissions) {
    UUID id = UUID.randomUUID();
    boolean isDefault = false;
    return new Role(id, name, isDefault, permissions);
  }

  public static Role restore(UUID id, String name, boolean isDefault, Set<Permission> permissions) {
    return new Role(id, name, isDefault, permissions);
  }

  private static String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new RoleNameRequiredException();
    }

    return name.trim();
  }

  public Set<Permission> getPermissions() {
    return Collections.unmodifiableSet(permissions);
  }
}
