package com.app.url_shortener.iam.application.port.output;

import com.app.url_shortener.iam.domain.model.Role;

import java.util.Optional;

public interface RoleRepositoryPort {

  Optional<Role> findByName(String name);

  Role findDefaultRole();
}
