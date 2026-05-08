package com.app.url_shortener.url.infrastructure.adapter;

import com.app.url_shortener.url.domain.exception.ShortCodeCollisionException;
import com.app.url_shortener.url.domain.model.Url;
import com.app.url_shortener.url.infrastructure.entity.UrlEntity;
import com.app.url_shortener.url.infrastructure.mapper.UrlMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Adaptador de Repositório de URL")
@SuppressWarnings({"unchecked", "rawtypes"})
class UrlRepositoryAdapterTest {

  @Mock
  private DynamoDbTable<UrlEntity> urlTable;

  @Mock
  private UrlMapper urlMapper;

  @InjectMocks
  private UrlRepositoryAdapter adapter;

  @Nested
  @DisplayName("Persistência")
  class SaveTests {

    @Test
    @DisplayName("Deve mapear domínio e salvar entidade com condição contra colisão de código curto")
    void shouldMapDomainAndSaveEntityWithShortCodeCollisionCondition() {
      // 1. Arrange
      var url = Url.restore("aB3dE", "https://google.com", LocalDateTime.of(2026, 5, 7, 10, 0));
      var entity = urlEntity("aB3dE", "https://google.com", "2026-05-07T10:00");
      when(urlMapper.toEntity(url)).thenReturn(entity);

      // 2. Act
      adapter.save(url);

      // 3. Assert
      var requestCaptor = ArgumentCaptor.forClass(PutItemEnhancedRequest.class);
      verify(urlMapper).toEntity(url);
      verify(urlTable).putItem(requestCaptor.capture());

      var request = (PutItemEnhancedRequest<UrlEntity>) requestCaptor.getValue();
      assertThat(request.item()).isSameAs(entity);
      assertThat(request.conditionExpression().expression()).isEqualTo("attribute_not_exists(#pk)");
      assertThat(request.conditionExpression().expressionNames())
          .containsEntry("#pk", "shortCode");
      verifyNoMoreInteractions(urlMapper, urlTable);
    }

    @Test
    @DisplayName("Deve traduzir falha condicional do DynamoDB para colisão de código curto")
    void shouldTranslateConditionalCheckFailureToShortCodeCollisionException() {
      // 1. Arrange
      var url = Url.restore("aB3dE", "https://google.com", LocalDateTime.of(2026, 5, 7, 10, 0));
      var entity = urlEntity("aB3dE", "https://google.com", "2026-05-07T10:00");
      var exception = ConditionalCheckFailedException.builder().message("collision").build();
      when(urlMapper.toEntity(url)).thenReturn(entity);
      doThrow(exception).when(urlTable).putItem(any(PutItemEnhancedRequest.class));

      // 2. Act & 3. Assert
      assertThatThrownBy(() -> adapter.save(url))
          .isInstanceOf(ShortCodeCollisionException.class);
      verify(urlMapper).toEntity(url);
      verify(urlTable).putItem(any(PutItemEnhancedRequest.class));
      verifyNoMoreInteractions(urlMapper, urlTable);
    }
  }

  @Nested
  @DisplayName("Busca por código curto")
  class FindByShortCodeTests {

    @Test
    @DisplayName("Deve buscar entidade no DynamoDB, mapear e retornar domínio")
    void shouldGetEntityFromDynamoDbMapAndReturnDomain() {
      // 1. Arrange
      var shortCode = "aB3dE";
      var entity = urlEntity(shortCode, "https://google.com", "2026-05-07T10:00");
      var url = Url.restore(shortCode, "https://google.com", LocalDateTime.of(2026, 5, 7, 10, 0));
      when(urlTable.getItem(anyGetItemRequestConsumer())).thenReturn(entity);
      when(urlMapper.toDomain(entity)).thenReturn(url);

      // 2. Act
      var result = adapter.findByShortCode(shortCode);

      // 3. Assert
      assertThat(result).containsSame(url);
      var consumerCaptor = getItemRequestConsumerCaptor();
      verify(urlTable).getItem(consumerCaptor.capture());
      assertThatGetItemRequestUsesShortCode(consumerCaptor.getValue(), shortCode);
      verify(urlMapper).toDomain(entity);
      verifyNoMoreInteractions(urlTable, urlMapper);
    }

    @Test
    @DisplayName("Deve retornar vazio quando DynamoDB não encontrar entidade")
    void shouldReturnEmptyWhenDynamoDbDoesNotFindEntity() {
      // 1. Arrange
      var shortCode = "missing";
      when(urlTable.getItem(anyGetItemRequestConsumer())).thenReturn(null);

      // 2. Act
      var result = adapter.findByShortCode(shortCode);

      // 3. Assert
      assertThat(result).isEmpty();
      var consumerCaptor = getItemRequestConsumerCaptor();
      verify(urlTable).getItem(consumerCaptor.capture());
      assertThatGetItemRequestUsesShortCode(consumerCaptor.getValue(), shortCode);
      verifyNoMoreInteractions(urlTable, urlMapper);
    }
  }

  private UrlEntity urlEntity(String shortCode, String originalUrl, String createdAt) {
    return UrlEntity.builder()
        .shortCode(shortCode)
        .originalUrl(originalUrl)
        .createdAt(createdAt)
        .build();
  }

  private Consumer<GetItemEnhancedRequest.Builder> anyGetItemRequestConsumer() {
    return any(Consumer.class);
  }

  private ArgumentCaptor<Consumer<GetItemEnhancedRequest.Builder>> getItemRequestConsumerCaptor() {
    return ArgumentCaptor.forClass((Class) Consumer.class);
  }

  private void assertThatGetItemRequestUsesShortCode(
      Consumer<GetItemEnhancedRequest.Builder> requestConsumer,
      String shortCode) {
    var requestBuilder = GetItemEnhancedRequest.builder();
    requestConsumer.accept(requestBuilder);
    var request = requestBuilder.build();

    assertThat(request.key().partitionKeyValue().s()).isEqualTo(shortCode);
  }
}
