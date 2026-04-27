package com.app.url_shortener.url.infrastructure.mapper;

import com.app.url_shortener.url.domain.model.Url;
import com.app.url_shortener.url.infrastructure.persistence.entity.UrlEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ObjectFactory;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UrlMapper {

  @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToString")
  UrlEntity toEntity(Url domain);

  Url toDomain(UrlEntity entity);

  @ObjectFactory
  default Url createUrl(UrlEntity entity) {
    if (entity == null) {
      return null;
    }
    return Url.create(
            entity.getShortCode(),
            entity.getOriginalUrl(),
            stringToLocalDateTime(entity.getCreatedAt()));
  }

  @Named("localDateTimeToString")
  default String localDateTimeToString(LocalDateTime value) {
    return value == null ? null : value.toString();
  }

  @Named("stringToLocalDateTime")
  default LocalDateTime stringToLocalDateTime(String value) {
    return value == null ? null : LocalDateTime.parse(value);
  }

}