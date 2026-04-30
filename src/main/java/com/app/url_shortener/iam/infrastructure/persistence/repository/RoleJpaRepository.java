package com.app.url_shortener.iam.infrastructure.persistence.repository;

import com.app.url_shortener.iam.infrastructure.persistence.entity.RoleEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {

  @EntityGraph(attributePaths = "permissions")
  Optional<RoleEntity> findByName(String name);

  @EntityGraph(attributePaths = "permissions")
  @Query("SELECT r FROM RoleEntity r WHERE r.isDefault = true")
  Optional<RoleEntity> findDefaultRole();
}
