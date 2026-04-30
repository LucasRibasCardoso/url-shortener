package com.app.url_shortener.url.infrastructure.mapper;

import com.app.url_shortener.url.domain.model.Url;
import com.app.url_shortener.url.infrastructure.entity.UrlEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UrlMapper {

  @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToString")
  UrlEntity toEntity(Url domain);

  default Url createUrl(UrlEntity entity) {
    if (entity == null) {
      return null;
    }

    return toDomain(entity);
  }

  default Url toDomain(UrlEntity entity) {
    if (entity == null) {
      return null;
    }

    return Url.restore(
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
