package com.app.url_shortener.user.infrastructure.repository;

import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  @EntityGraph(attributePaths = {
          "roles",
          "roles.permissions"
  })
  @Query("select u from UserEntity u where u.email = :email")
  Optional<UserEntity> findByEmailWithRolesAndPermissions(@Param("email") String email);

  @EntityGraph(attributePaths = "roles")
  @Query("select u from UserEntity u where u.email = :email")
  Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);
}
