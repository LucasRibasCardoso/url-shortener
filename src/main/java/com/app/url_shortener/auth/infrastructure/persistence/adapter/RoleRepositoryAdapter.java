package com.app.url_shortener.auth.infrastructure.persistence.adapter;

import com.app.url_shortener.auth.application.port.output.RoleRepositoryPort;
import com.app.url_shortener.auth.domain.exception.DefaultRoleNotFoundException;
import com.app.url_shortener.auth.domain.model.Role;
import com.app.url_shortener.auth.infrastructure.persistence.mapper.RolePersistenceMapper;
import com.app.url_shortener.auth.infrastructure.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

  private final RoleJpaRepository roleJpaRepository;
  private final RolePersistenceMapper rolePersistenceMapper;

  @Override
  public Optional<Role> findByName(String name) {
    return roleJpaRepository.findByName(name).map(rolePersistenceMapper::toDomain);
  }

  @Override
  public Role findDefaultRole() {
    return roleJpaRepository.findDefaultRole()
            .map(rolePersistenceMapper::toDomain)
            .orElseThrow(DefaultRoleNotFoundException::new);
  }
}
