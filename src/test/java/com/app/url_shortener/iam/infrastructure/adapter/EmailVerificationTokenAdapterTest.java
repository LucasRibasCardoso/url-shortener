package com.app.url_shortener.iam.infrastructure.adapter;

import com.app.url_shortener.config.BaseRedisSliceTest;
import com.app.url_shortener.iam.domain.valueobject.EmailVerificationToken;
import com.app.url_shortener.iam.domain.valueobject.VerificationCode;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("redis-slice")
@Import(EmailVerificationTokenAdapter.class)
@DisplayName("Slice Redis - Adaptador de Token de Verificação de Email")
class EmailVerificationTokenAdapterTest extends BaseRedisSliceTest {

  private static final String KEY_PREFIX = "auth:email-verification:";
  private static final String KEY_PATTERN = KEY_PREFIX + "*";

  @Autowired
  private EmailVerificationTokenAdapter adapter;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    deleteEmailVerificationKeys();
  }

  @AfterEach
  void tearDown() {
    deleteEmailVerificationKeys();
  }

  @Nested
  @DisplayName("Armazenamento")
  class StoreTests {

    @Test
    @DisplayName("Deve criar chave no Redis com valor serializado e TTL")
    void shouldCreateRedisKeyWithSerializedValueAndTtl() {
      // 1. Arrange
      var ttl = Duration.ofMinutes(15);
      var token = emailVerificationToken("user@email.com");
      var key = key(token.email());

      // 2. Act
      adapter.store(token, ttl);

      // 3. Assert
      assertThat(redisTemplate.hasKey(key)).isTrue();
      assertThat(redisTemplate.opsForValue().get(key)).isEqualTo(serializedValue(token));

      var ttlMillis = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
      assertThat(ttlMillis)
              .isNotNull()
              .isPositive()
              .isLessThanOrEqualTo(ttl.toMillis())
              .isGreaterThan(ttl.minusSeconds(5).toMillis());
    }

    @Test
    @DisplayName("Deve manter token legível antes da expiração")
    void shouldReadTokenBeforeExpiration() {
      // 1. Arrange
      var token = emailVerificationToken("before-expiration@email.com");
      adapter.store(token, Duration.ofMinutes(5));

      // 2. Act
      var result = adapter.findByEmail(token.email());

      // 3. Assert
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(token);
      assertThat(redisTemplate.getExpire(key(token.email()), TimeUnit.MILLISECONDS)).isPositive();
    }
  }

  @Nested
  @DisplayName("Leitura")
  class FindByEmailTests {

    @Test
    @DisplayName("Deve desserializar token salvo diretamente no Redis")
    void shouldDeserializeTokenStoredDirectlyInRedis() {
      // 1. Arrange
      var token = emailVerificationToken("serialized@email.com");
      var key = key(token.email());
      redisTemplate.opsForValue().set(key, serializedValue(token), Duration.ofMinutes(10));

      // 2. Act
      var result = adapter.findByEmail(token.email());

      // 3. Assert
      assertThat(result).isPresent();
      assertThat(result.get().userId()).isEqualTo(token.userId());
      assertThat(result.get().email()).isEqualTo(token.email());
      assertThat(result.get().code()).isEqualTo(token.code());
      assertThat(result.get().expiresAt()).isEqualTo(token.expiresAt());
    }

    @Test
    @DisplayName("Deve retornar vazio quando chave não existir")
    void shouldReturnEmptyWhenKeyDoesNotExist() {
      // 1. Arrange
      var email = "missing@email.com";

      // 2. Act
      var result = adapter.findByEmail(email);

      // 3. Assert
      assertThat(result).isEmpty();
      assertThat(redisTemplate.hasKey(key(email))).isFalse();
    }
  }

  @Nested
  @DisplayName("Remoção e expiração")
  class DeleteAndExpirationTests {

    @Test
    @DisplayName("Deve remover chave e retornar vazio após deleção")
    void shouldRemoveKeyAndReturnEmptyAfterDeletion() {
      // 1. Arrange
      var token = emailVerificationToken("delete@email.com");
      var key = key(token.email());
      adapter.store(token, Duration.ofMinutes(5));

      // 2. Act
      adapter.deleteByEmail(token.email());

      // 3. Assert
      assertThat(redisTemplate.hasKey(key)).isFalse();
      assertThat(redisTemplate.getExpire(key, TimeUnit.MILLISECONDS)).isEqualTo(-2L);
      assertThat(adapter.findByEmail(token.email())).isEmpty();
    }

    @Test
    @DisplayName("Deve deixar token indisponível após expiração curta")
    void shouldMakeTokenUnavailableAfterShortExpiration() throws InterruptedException {
      // 1. Arrange
      var token = emailVerificationToken("expires@email.com");
      var key = key(token.email());
      adapter.store(token, Duration.ofMillis(50));

      // 2. Act
      waitUntilKeyIsMissing(key);

      // 3. Assert
      assertThat(redisTemplate.hasKey(key)).isFalse();
      assertThat(redisTemplate.getExpire(key, TimeUnit.MILLISECONDS)).isEqualTo(-2L);
      assertThat(adapter.findByEmail(token.email())).isEmpty();
    }
  }

  private void waitUntilKeyIsMissing(String key) throws InterruptedException {
    long deadline = System.nanoTime() + Duration.ofSeconds(2).toNanos();

    while (Boolean.TRUE.equals(redisTemplate.hasKey(key)) && System.nanoTime() < deadline) {
      Thread.sleep(10);
    }
  }

  private void deleteEmailVerificationKeys() {
    Set<String> keys = redisTemplate.keys(KEY_PATTERN);
    if (keys != null && !keys.isEmpty()) {
      redisTemplate.delete(keys);
    }
  }

  private EmailVerificationToken emailVerificationToken(String email) {
    return EmailVerificationToken.create(
            UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123"),
            email,
            VerificationCode.of("123456"),
            Instant.parse("2026-05-07T18:00:00Z")
    );
  }

  private String serializedValue(EmailVerificationToken token) {
    return token.userId()
            + "|"
            + token.code().value()
            + "|"
            + token.expiresAt();
  }

  private String key(String email) {
    return KEY_PREFIX + email;
  }
}