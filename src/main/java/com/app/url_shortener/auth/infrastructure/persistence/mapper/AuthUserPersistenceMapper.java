package com.app.url_shortener.auth.infrastructure.persistence.mapper;

import com.app.url_shortener.auth.domain.model.AuthUser;
import com.app.url_shortener.auth.domain.model.Role;
import com.app.url_shortener.user.infrastructure.entity.UserEntity;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = RolePersistenceMapper.class)
public interface AuthUserPersistenceMapper {

  default AuthUser toDomain(UserEntity entity) {
    if (entity == null) {
      return null;
    }

    return AuthUser.restore(
        entity.getId(),
        entity.getName(),
        entity.getEmail(),
        entity.getPasswordHash(),
        entity.isEmailVerified(),
        toDomainRoles(entity.getRoles()));
  }

  default UserEntity toPendingRegistrationEntity(AuthUser authUser) {
    if (authUser == null) {
      return null;
    }

    return UserEntity.createPendingRegistration(
        authUser.getName(),
        authUser.getEmail(),
        authUser.getPasswordHash());
  }

  Set<Role> toDomainRoles(Set<com.app.url_shortener.auth.infrastructure.persistence.entity.RoleEntity> entities);
}
