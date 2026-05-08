package com.app.url_shortener.shared.infrastructure.idempotency.impl;

import com.app.url_shortener.config.BaseRedisSliceTest;
import com.app.url_shortener.shared.exception.internalservererror.IdempotencyCacheException;
import com.app.url_shortener.shared.infrastructure.idempotency.CachedResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("redis-slice")
@Import({
        RedisIdempotencyStore.class,
        RedisIdempotencyStoreObjectMapperTestConfig.class
})
@DisplayName("Slice Redis - Armazenamento de Idempotência")
class RedisIdempotencyStoreTest extends BaseRedisSliceTest {

  private static final String KEY_PREFIX = "idempotency:";
  private static final String KEY_PATTERN = KEY_PREFIX + "*";

  @Autowired
  private RedisIdempotencyStore store;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    deleteIdempotencyKeys();
  }

  @AfterEach
  void tearDown() {
    deleteIdempotencyKeys();
  }

  @Nested
  @DisplayName("Estado em progresso")
  class SaveInProgressTests {

    @Test
    @DisplayName("Deve salvar estado em progresso quando chave não existir")
    void shouldSaveInProgressWhenKeyDoesNotExist() {
      // 1. Arrange
      var key = "create-user-absent";
      var redisKey = redisKey(key);
      var timeoutInMinutes = 5L;

      // 2. Act
      var result = store.saveInProgress(key, timeoutInMinutes);

      // 3. Assert
      assertThat(result).isTrue();
      assertThat(redisTemplate.opsForValue().get(redisKey)).isEqualTo("IN_PROGRESS");

      var ttlMillis = redisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
      assertThat(ttlMillis)
              .isNotNull()
              .isPositive()
              .isLessThanOrEqualTo(Duration.ofMinutes(timeoutInMinutes).toMillis())
              .isGreaterThan(Duration.ofMinutes(timeoutInMinutes).minusSeconds(5).toMillis());
    }

    @Test
    @DisplayName("Deve retornar falso quando chave já existir")
    void shouldReturnFalseWhenKeyAlreadyExists() {
      // 1. Arrange
      var key = "create-user-existing";
      var firstResult = store.saveInProgress(key, 5);

      // 2. Act
      var secondResult = store.saveInProgress(key, 5);

      // 3. Assert
      assertThat(firstResult).isTrue();
      assertThat(secondResult).isFalse();
      assertThat(redisTemplate.opsForValue().get(redisKey(key))).isEqualTo("IN_PROGRESS");
    }
  }

  @Nested
  @DisplayName("Leitura de estado")
  class GetStateTests {

    @Test
    @DisplayName("Deve desserializar resposta em cache salva como JSON no Redis")
    void shouldDeserializeCachedResponseFromRedisJson() throws Exception {
      // 1. Arrange
      var key = "completed-json";
      var cachedResponse = cachedResponse(201, "{\"id\":\"123\"}");
      redisTemplate.opsForValue().set(redisKey(key), objectMapper.writeValueAsString(cachedResponse));

      // 2. Act
      var result = store.getState(key);

      // 3. Assert
      assertThat(result).isNotNull();
      assertThat(result.status()).isEqualTo(201);
      assertThat(result.body()).containsExactly(cachedResponse.body());
    }

    @Test
    @DisplayName("Deve retornar nulo quando estado for em progresso")
    void shouldReturnNullWhenStateIsInProgress() {
      // 1. Arrange
      var key = "in-progress-state";
      redisTemplate.opsForValue().set(redisKey(key), "IN_PROGRESS");

      // 2. Act
      var result = store.getState(key);

      // 3. Assert
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Deve retornar nulo quando chave não existir")
    void shouldReturnNullWhenKeyDoesNotExist() {
      // 1. Arrange
      var key = "missing-state";

      // 2. Act
      var result = store.getState(key);

      // 3. Assert
      assertThat(result).isNull();
      assertThat(redisTemplate.hasKey(redisKey(key))).isFalse();
    }

    @Test
    @DisplayName("Deve lançar IdempotencyCacheException quando JSON estiver inválido")
    void shouldThrowIdempotencyCacheExceptionWhenJsonIsInvalid() {
      // 1. Arrange
      var key = "bad-json";
      redisTemplate.opsForValue().set(redisKey(key), "{invalid-json");

      // 2. Act & 3. Assert
      assertThatThrownBy(() -> store.getState(key))
              .isInstanceOf(IdempotencyCacheException.class)
              .hasMessage("Falha ao desserializar o cache de idempotency-key");
    }
  }

  @Nested
  @DisplayName("Estado concluído")
  class SaveCompletedTests {

    @Test
    @DisplayName("Deve serializar resposta em cache e salvar com TTL")
    void shouldSerializeCachedResponseAndSaveWithTtl() throws Exception {
      // 1. Arrange
      var key = "completed-save";
      var redisKey = redisKey(key);
      var cachedResponse = cachedResponse(200, "{\"shortCode\":\"abc123\"}");
      var ttlInHours = 1L;

      // 2. Act
      store.saveCompleted(key, cachedResponse, ttlInHours);

      // 3. Assert
      var storedJson = redisTemplate.opsForValue().get(redisKey);
      assertThat(storedJson).isNotBlank();

      var storedResponse = objectMapper.readValue(storedJson, CachedResponse.class);
      assertThat(storedResponse.status()).isEqualTo(200);
      assertThat(storedResponse.body()).containsExactly(cachedResponse.body());

      var ttlMillis = redisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
      assertThat(ttlMillis)
              .isNotNull()
              .isPositive()
              .isLessThanOrEqualTo(Duration.ofHours(ttlInHours).toMillis())
              .isGreaterThan(Duration.ofHours(ttlInHours).minusSeconds(5).toMillis());
    }
  }

  @Nested
  @DisplayName("Remoção")
  class DeleteTests {

    @Test
    @DisplayName("Deve remover chave do Redis")
    void shouldRemoveKeyFromRedis() {
      // 1. Arrange
      var key = "delete-key";
      var redisKey = redisKey(key);
      store.saveInProgress(key, 5);

      // 2. Act
      store.delete(key);

      // 3. Assert
      assertThat(redisTemplate.hasKey(redisKey)).isFalse();
      assertThat(redisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS)).isEqualTo(-2L);
    }
  }

  private void deleteIdempotencyKeys() {
    Set<String> keys = redisTemplate.keys(KEY_PATTERN);
    if (keys != null && !keys.isEmpty()) {
      redisTemplate.delete(keys);
    }
  }

  private static CachedResponse cachedResponse(int status, String body) {
    return new CachedResponse(status, body.getBytes(StandardCharsets.UTF_8));
  }

  private static String redisKey(String key) {
    return KEY_PREFIX + key;
  }

}

@TestConfiguration
class RedisIdempotencyStoreObjectMapperTestConfig {

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
