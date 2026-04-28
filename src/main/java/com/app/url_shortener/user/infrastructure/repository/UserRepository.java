package com.app.url_shortener.user.infrastructure.repository;

import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  @EntityGraph(attributePaths = {
          "roles",
          "roles.permissions"
  })
  Optional<UserEntity> findByEmailWithRolesAndPermissions(String email);
}
