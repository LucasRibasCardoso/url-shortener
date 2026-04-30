package com.app.url_shortener.auth.infrastructure.persistence.mapper;

import com.app.url_shortener.auth.domain.model.Permission;
import com.app.url_shortener.auth.domain.model.Role;
import com.app.url_shortener.auth.infrastructure.persistence.entity.PermissionEntity;
import com.app.url_shortener.auth.infrastructure.persistence.entity.RoleEntity;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RolePersistenceMapper {

  default Role toDomain(RoleEntity entity) {
    if (entity == null) {
      return null;
    }

    return Role.create(entity.getId(), entity.getName(), toDomainPermissions(entity.getPermissions()));
  }

  default Permission toDomain(PermissionEntity entity) {
    if (entity == null) {
      return null;
    }

    return Permission.create(entity.getId(), entity.getName(), entity.getDescription());
  }

  Set<Permission> toDomainPermissions(Set<PermissionEntity> entities);
}
