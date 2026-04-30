package com.app.url_shortener.iam.infrastructure.persistence.mapper;

import com.app.url_shortener.iam.domain.model.Permission;
import com.app.url_shortener.iam.domain.model.Role;
import com.app.url_shortener.iam.infrastructure.persistence.entity.PermissionEntity;
import com.app.url_shortener.iam.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = PermissionPersistenceMapper.class
)
public abstract class RolePersistenceMapper {

  @Mapping(target = "permissions", ignore = true)
  public abstract Role toDomain(RoleEntity entity);

  // Ignora os campos gerenciados pelo JPA Auditing
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "permissions", ignore = true)
  public abstract RoleEntity toEntity(Role domain);

  public abstract Set<Permission> toDomainPermissions(Set<PermissionEntity> entities);

  public abstract Set<PermissionEntity> toEntityPermissions(Set<Permission> domains);


  @ObjectFactory
  public Role createDomainObject(RoleEntity entity) {
    if (entity == null) {
      return null;
    }
    return Role.restore(
            entity.getId(),
            entity.getName(),
            entity.isDefault(),
            toDomainPermissions(entity.getPermissions())
    );
  }

  @ObjectFactory
  public RoleEntity createEntityObject(Role domain) {
    if (domain == null) {
      return null;
    }
    return new RoleEntity(
            domain.getId(),
            domain.getName(),
            domain.isDefault(),
            toEntityPermissions(domain.getPermissions())
    );
  }
}
