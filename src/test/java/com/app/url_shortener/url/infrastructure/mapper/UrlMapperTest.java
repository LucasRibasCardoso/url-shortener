package com.app.url_shortener.url.infrastructure.mapper;

import com.app.url_shortener.url.domain.model.Url;
import com.app.url_shortener.url.infrastructure.entity.UrlEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@DisplayName("Testes de Unidade - Mapper de URL")
class UrlMapperTest {

  private UrlMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new UrlMapperImpl();
  }

  @Nested
  @DisplayName("Mapeamento para entidade")
  class ToEntityTests {

    @Test
    @DisplayName("Deve mapear domínio para entidade com data convertida para texto")
    void shouldMapDomainToEntityWithCreatedAtConvertedToString() {
      // 1. Arrange
      var createdAt = LocalDateTime.of(2026, 5, 7, 10, 15, 30);
      var url = Url.restore("aB3dE", "https://google.com", createdAt);

      // 2. Act
      var result = mapper.toEntity(url);

      // 3. Assert
      assertThat(result.getShortCode()).isEqualTo("aB3dE");
      assertThat(result.getOriginalUrl()).isEqualTo("https://google.com");
      assertThat(result.getCreatedAt()).isEqualTo("2026-05-07T10:15:30");
    }

    @Test
    @DisplayName("Deve retornar nulo quando domínio for nulo")
    void shouldReturnNullWhenDomainIsNull() {
      // 1. Arrange
      Url url = null;

      // 2. Act
      var result = mapper.toEntity(url);

      // 3. Assert
      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("Mapeamento para domínio")
  class ToDomainTests {

    @Test
    @DisplayName("Deve mapear entidade para domínio com data convertida para LocalDateTime")
    void shouldMapEntityToDomainWithCreatedAtConvertedToLocalDateTime() {
      // 1. Arrange
      var entity = urlEntity();

      // 2. Act
      var result = mapper.toDomain(entity);

      // 3. Assert
      assertThat(result.getShortCode()).isEqualTo("aB3dE");
      assertThat(result.getOriginalUrl()).isEqualTo("https://google.com");
      assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 7, 10, 15, 30));
    }

    @Test
    @DisplayName("Deve retornar nulo quando entidade for nula")
    void shouldReturnNullWhenEntityIsNull() {
      // 1. Arrange
      UrlEntity entity = null;

      // 2. Act
      var result = mapper.toDomain(entity);

      // 3. Assert
      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("Criação de URL")
  class CreateUrlTests {

    @Test
    @DisplayName("Deve criar domínio a partir da entidade")
    void shouldCreateDomainFromEntity() {
      // 1. Arrange
      var entity = urlEntity();

      // 2. Act
      var result = mapper.createUrl(entity);

      // 3. Assert
      assertThat(result.getShortCode()).isEqualTo("aB3dE");
      assertThat(result.getOriginalUrl()).isEqualTo("https://google.com");
      assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 7, 10, 15, 30));
    }

    @Test
    @DisplayName("Deve retornar nulo quando entidade for nula")
    void shouldReturnNullWhenEntityIsNull() {
      // 1. Arrange
      UrlEntity entity = null;

      // 2. Act
      var result = mapper.createUrl(entity);

      // 3. Assert
      assertThat(result).isNull();
    }
  }

  @Nested
  @DisplayName("Conversão de datas")
  class DateConversionTests {

    @Test
    @DisplayName("Deve converter LocalDateTime para texto")
    void shouldConvertLocalDateTimeToString() {
      // 1. Arrange
      var value = LocalDateTime.of(2026, 5, 7, 10, 15, 30);

      // 2. Act
      var result = mapper.localDateTimeToString(value);

      // 3. Assert
      assertThat(result).isEqualTo("2026-05-07T10:15:30");
    }

    @Test
    @DisplayName("Deve converter texto para LocalDateTime")
    void shouldConvertStringToLocalDateTime() {
      // 1. Arrange
      var value = "2026-05-07T10:15:30";

      // 2. Act
      var result = mapper.stringToLocalDateTime(value);

      // 3. Assert
      assertThat(result).isEqualTo(LocalDateTime.of(2026, 5, 7, 10, 15, 30));
    }

    @Test
    @DisplayName("Deve retornar nulo ao converter LocalDateTime nulo para texto")
    void shouldReturnNullWhenConvertingNullLocalDateTimeToString() {
      // 1. Arrange
      LocalDateTime value = null;

      // 2. Act
      var result = mapper.localDateTimeToString(value);

      // 3. Assert
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Deve retornar nulo ao converter texto nulo para LocalDateTime")
    void shouldReturnNullWhenConvertingNullStringToLocalDateTime() {
      // 1. Arrange
      String value = null;

      // 2. Act
      var result = mapper.stringToLocalDateTime(value);

      // 3. Assert
      assertThat(result).isNull();
    }
  }

  private UrlEntity urlEntity() {
    return UrlEntity.builder()
        .shortCode("aB3dE")
        .originalUrl("https://google.com")
        .createdAt("2026-05-07T10:15:30")
        .build();
  }
}
