package com.app.url_shortener.unitTest.iam.domain.model;

import com.app.url_shortener.iam.domain.exception.auth.RefreshTokenExpiredException;
import com.app.url_shortener.iam.domain.exception.auth.TokenCompromisedException;
import com.app.url_shortener.iam.domain.model.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Tag("unit")
@DisplayName("Testes de Unidade - Entidade RefreshToken")
class RefreshTokenTest {

  @Nested
  @DisplayName("Criação de Token")
  class CreationTests {

    @Test
    @DisplayName("Deve criar um novo token válido com expiração de 7 dias")
    void shouldCreateValidTokenWith7DaysExpiration() {
      // Arrange
      var userId = UUID.randomUUID();
      var tokenHash = "hash-seguro-123";
      var now = Instant.now();

      // Act
      var token = RefreshToken.create(userId, tokenHash);

      // Assert
      assertThat(token).isNotNull();
      assertThat(token.getId()).isNotNull();
      assertThat(token.getUserId()).isEqualTo(userId);
      assertThat(token.getTokenHash()).isEqualTo(tokenHash);
      assertThat(token.isRevoked()).isFalse();

      var expectedExpiration = now.plus(7, ChronoUnit.DAYS);
      assertThat(token.getExpiresAt()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));
    }
  }

  @Nested
  @DisplayName("Rotação de Token (Token Rotation)")
  class RotationTests {

    @Test
    @DisplayName("Deve rotacionar o token com sucesso, revogando o atual e gerando um novo")
    void shouldRotateTokenSuccessfully() {
      // Arrange
      var userId = UUID.randomUUID();
      var currentToken = RefreshToken.create(userId, "hash-antigo");
      var newTokenHash = "hash-novo-456";

      // Act
      var nextToken = currentToken.rotate(newTokenHash);

      // Assert
      assertThat(currentToken.isRevoked()).isTrue();
      assertThat(currentToken.getRevokedAt()).isNotNull();
      assertThat(currentToken.getReplacedByTokenId()).isEqualTo(nextToken.getId());

      assertThat(nextToken).isNotNull();
      assertThat(nextToken.getUserId()).isEqualTo(userId);
      assertThat(nextToken.getTokenHash()).isEqualTo(newTokenHash);
      assertThat(nextToken.isRevoked()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção de comprometimento ao tentar rotacionar um token já revogado")
    void shouldThrowExceptionWhenRotatingRevokedToken() {
      // Arrange
      var currentToken = RefreshToken.create(UUID.randomUUID(), "hash");
      // Forçamos a revogação simulando um restore com a data de revogação preenchida
      var revokedToken = RefreshToken.restore(
              currentToken.getId(),
              currentToken.getUserId(),
              currentToken.getTokenHash(),
              currentToken.getCreatedAt(),
              currentToken.getExpiresAt(),
              Instant.now(),
              null
      );

      // Act & Assert
      assertThatThrownBy(() -> revokedToken.rotate("novo-hash")).isInstanceOf(TokenCompromisedException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção de expiração ao tentar rotacionar um token com tempo limite ultrapassado")
    void shouldThrowExceptionWhenRotatingExpiredToken() {
      // 1. Arrange
      var pastExpiration = Instant.now().minus(1, ChronoUnit.DAYS);
      var expiredToken = RefreshToken.restore(
              UUID.randomUUID(),
              UUID.randomUUID(),
              "hash",
              Instant.now().minus(8, ChronoUnit.DAYS),
              pastExpiration, // expirou ontem
              null,
              null
      );

      // 2 & 3. Act & Assert
      assertThatThrownBy(() -> expiredToken.rotate("novo-hash")).isInstanceOf(RefreshTokenExpiredException.class);
    }
  }
}