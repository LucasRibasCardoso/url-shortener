package com.app.url_shortener.iam.infrastructure.persistence.mapper;

import com.app.url_shortener.iam.domain.model.Permission;
import com.app.url_shortener.iam.infrastructure.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class PermissionPersistenceMapper {

  public abstract Permission toDomain(PermissionEntity entity);

  // Ignora os campos gerenciados pelo JPA Auditing
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract PermissionEntity toEntity(Permission domain);

  @ObjectFactory
  public Permission createDomainObject(PermissionEntity entity) {
    if (entity == null) {
      return null;
    }
    return Permission.restore(
            entity.getId(),
            entity.getName(),
            entity.getDescription()
    );
  }

  @ObjectFactory
  public PermissionEntity createEntityObject(Permission domain) {
    if (domain == null) {
      return null;
    }
    return new PermissionEntity(
            domain.getId(),
            domain.getName(),
            domain.getDescription()
    );
  }
}
