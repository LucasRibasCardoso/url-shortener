package com.app.url_shortener.auth.application.port.output;

import com.app.url_shortener.auth.domain.model.Role;
import com.app.url_shortener.auth.infrastructure.persistence.entity.RoleEntity;

import java.util.Optional;

public interface RoleRepositoryPort {

  Optional<Role> findByName(String name);

  Role findDefaultRole();
}
