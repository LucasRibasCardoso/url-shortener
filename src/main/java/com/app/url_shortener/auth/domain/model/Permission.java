package com.app.url_shortener.auth.domain.model;

import com.app.url_shortener.auth.domain.exception.PermissionNameRequiredException;
import java.util.Objects;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Permission {

  private final UUID id;
  private final String name;
  private final String description;

  private Permission(UUID id, String name, String description) {
    this.id = validateId(id);
    this.name = validateName(name);
    this.description = normalizeDescription(description);
  }

  public static Permission create(UUID id, String name, String description) {
    return new Permission(id, name, description);
  }

  private static String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new PermissionNameRequiredException();
    }

    return name.trim();
  }

  private static UUID validateId(UUID id) {
    return Objects.requireNonNull(id, "id is required");
  }

  private static String normalizeDescription(String description) {
    if (description == null) {
      return null;
    }

    String normalizedDescription = description.trim();
    return normalizedDescription.isBlank() ? null : normalizedDescription;
  }
}
