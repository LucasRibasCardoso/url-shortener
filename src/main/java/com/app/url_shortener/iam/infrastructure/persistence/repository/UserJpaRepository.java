package com.app.url_shortener.iam.infrastructure.persistence.repository;

import com.app.url_shortener.iam.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
  @EntityGraph(attributePaths = {
          "roles",
          "roles.permissions"
  })
  @Query("select u from UserEntity u where u.email = :email")
  Optional<UserEntity> findByEmailWithRolesAndPermissions(@Param("email") String email);

}
