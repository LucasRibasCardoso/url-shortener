package com.app.url_shortener.iam.infrastructure.persistence.mapper;

import com.app.url_shortener.iam.domain.model.Role;
import com.app.url_shortener.iam.domain.model.UserAccount;
import com.app.url_shortener.iam.infrastructure.persistence.entity.RoleEntity;
import com.app.url_shortener.iam.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = RolePersistenceMapper.class)
public abstract class UserAccountPersistenceMapper {

  @Mapping(target = "roles", ignore = true)
  public abstract UserAccount toDomain(UserEntity entity);

  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  public abstract UserEntity toEntity(UserAccount domain);

  public abstract Set<Role> toDomainRoles(Set<RoleEntity> entities);

  public abstract Set<RoleEntity> toEntityRoles(Set<Role> domains);

  @ObjectFactory
  public UserAccount createDomainObject(UserEntity entity) {
    if (entity == null) {
      return null;
    }
    return UserAccount.restore(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getStatus(),
            entity.getPlan(),
            entity.isEmailVerified(),
            entity.getTokenVersion(),
            toDomainRoles(entity.getRoles())
    );
  }

  @ObjectFactory
  public UserEntity createEntityObject(UserAccount domain) {
    if (domain == null) {
      return null;
    }
    return new UserEntity(
            domain.getId(),
            domain.getName(),
            domain.getEmail(),
            domain.getPasswordHash(),
            domain.getStatus(),
            domain.getPlan(),
            domain.isEmailVerified(),
            domain.getTokenVersion(),
            null,
            null,
            toEntityRoles(domain.getRoles())
    );
  }
}