package com.app.url_shortener.unitTest.iam.infrastructure.adapter;

import com.app.url_shortener.iam.domain.valueobject.EmailVerificationToken;
import com.app.url_shortener.iam.domain.valueobject.VerificationCode;
import com.app.url_shortener.iam.infrastructure.adapter.EmailVerificationTokenAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Adaptador de Token de Verificação de Email")
class EmailVerificationTokenAdapterTest {

  private static final String KEY_PREFIX = "auth:email-verification:";

  @Mock
  private StringRedisTemplate redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private EmailVerificationTokenAdapter adapter;

  @Nested
  @DisplayName("Armazenamento")
  class StoreTests {

    @Test
    @DisplayName("Deve armazenar token serializado no Redis com TTL")
    void shouldStoreSerializedTokenInRedisWithTtl() {
      // 1. Arrange
      var ttl = Duration.ofMinutes(15);
      var token = emailVerificationToken();
      var expectedValue = token.userId()
              + "|"
              + token.code().value()
              + "|"
              + token.expiresAt();

      given(redisTemplate.opsForValue()).willReturn(valueOperations);

      // 2. Act
      adapter.store(token, ttl);

      // 3. Assert
      verify(redisTemplate).opsForValue();
      verify(valueOperations).set(KEY_PREFIX + token.email(), expectedValue, ttl);
      verifyNoMoreInteractions(redisTemplate, valueOperations);
    }
  }

  @Nested
  @DisplayName("Busca por Email")
  class FindByEmailTests {

    @Test
    @DisplayName("Deve retornar token quando valor válido existir no Redis")
    void shouldReturnTokenWhenValidValueExistsInRedis() {
      // 1. Arrange
      var email = "user@email.com";
      var userId = UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123");
      var code = "123456";
      var expiresAt = Instant.parse("2026-05-07T18:00:00Z");
      var redisValue = userId + "|" + code + "|" + expiresAt;

      given(redisTemplate.opsForValue()).willReturn(valueOperations);
      given(valueOperations.get(KEY_PREFIX + email)).willReturn(redisValue);

      // 2. Act
      var result = adapter.findByEmail(email);

      // 3. Assert
      assertThat(result).isPresent();
      assertThat(result.get().userId()).isEqualTo(userId);
      assertThat(result.get().email()).isEqualTo(email);
      assertThat(result.get().code()).isEqualTo(VerificationCode.of(code));
      assertThat(result.get().expiresAt()).isEqualTo(expiresAt);

      verify(redisTemplate).opsForValue();
      verify(valueOperations).get(KEY_PREFIX + email);
      verifyNoMoreInteractions(redisTemplate, valueOperations);
    }

    @Test
    @DisplayName("Deve retornar vazio quando valor não existir no Redis")
    void shouldReturnEmptyWhenValueDoesNotExistInRedis() {
      // 1. Arrange
      var email = "user@email.com";

      given(redisTemplate.opsForValue()).willReturn(valueOperations);
      given(valueOperations.get(KEY_PREFIX + email)).willReturn(null);

      // 2. Act
      var result = adapter.findByEmail(email);

      // 3. Assert
      assertThat(result).isEmpty();

      verify(redisTemplate).opsForValue();
      verify(valueOperations).get(KEY_PREFIX + email);
      verifyNoMoreInteractions(redisTemplate, valueOperations);
    }

    @Test
    @DisplayName("Deve retornar vazio quando valor do Redis estiver malformado")
    void shouldReturnEmptyWhenRedisValueIsMalformed() {
      // 1. Arrange
      var email = "user@email.com";

      given(redisTemplate.opsForValue()).willReturn(valueOperations);
      given(valueOperations.get(KEY_PREFIX + email)).willReturn("malformed-value");

      // 2. Act
      var result = adapter.findByEmail(email);

      // 3. Assert
      assertThat(result).isEmpty();

      verify(redisTemplate).opsForValue();
      verify(valueOperations).get(KEY_PREFIX + email);
      verifyNoMoreInteractions(redisTemplate, valueOperations);
    }
  }

  @Nested
  @DisplayName("Remoção por Email")
  class DeleteByEmailTests {

    @Test
    @DisplayName("Deve remover token do Redis usando chave de verificação de email")
    void shouldDeleteTokenFromRedisUsingEmailVerificationKey() {
      // 1. Arrange
      var email = "user@email.com";

      // 2. Act
      adapter.deleteByEmail(email);

      // 3. Assert
      verify(redisTemplate).delete(KEY_PREFIX + email);
      verifyNoMoreInteractions(redisTemplate, valueOperations);
    }
  }

  private EmailVerificationToken emailVerificationToken() {
    return EmailVerificationToken.create(
            UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123"),
            "user@email.com",
            VerificationCode.of("123456"),
            Instant.parse("2026-05-07T18:00:00Z")
    );
  }
}
