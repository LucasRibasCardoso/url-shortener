package com.app.url_shortener.unitTest.iam.infrastructure.adapter;

import com.app.url_shortener.iam.infrastructure.adapter.RefreshTokenCleanupTask;
import com.app.url_shortener.iam.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Tarefa de Limpeza de Refresh Tokens")
class RefreshTokenCleanupTaskTest {

  @Mock
  private RefreshTokenJpaRepository refreshTokenJpaRepository;

  @Captor
  private ArgumentCaptor<Instant> cutoffDateCaptor;

  @InjectMocks
  private RefreshTokenCleanupTask cleanupTask;

  @Nested
  @DisplayName("Limpeza de Tokens Expirados")
  class CleanupExpiredTokensTests {

    @Test
    @DisplayName("Deve remover tokens expirados antes de sete dias atrás")
    void shouldDeleteExpiredTokensBeforeSevenDaysAgo() {
      // 1. Arrange
      var earliestExpectedCutoff = Instant.now().minus(7, ChronoUnit.DAYS);

      given(refreshTokenJpaRepository.deleteExpiredTokensBefore(cutoffDateCaptor.capture()))
              .willReturn(3);

      // 2. Act
      cleanupTask.cleanupExpiredTokens();

      // 3. Assert
      var latestExpectedCutoff = Instant.now().minus(7, ChronoUnit.DAYS);
      var capturedCutoffDate = cutoffDateCaptor.getValue();

      assertThat(capturedCutoffDate)
              .isBetween(earliestExpectedCutoff, latestExpectedCutoff);

      verify(refreshTokenJpaRepository).deleteExpiredTokensBefore(capturedCutoffDate);
      verifyNoMoreInteractions(refreshTokenJpaRepository);
    }

    @Test
    @DisplayName("Deve propagar exceção quando a remoção dos tokens falhar")
    void shouldPropagateExceptionWhenDeletingExpiredTokensFails() {
      // 1. Arrange
      var exception = new IllegalStateException("Falha ao remover refresh tokens expirados.");

      given(refreshTokenJpaRepository.deleteExpiredTokensBefore(cutoffDateCaptor.capture()))
              .willThrow(exception);

      // 2. Act
      var throwableAssert = assertThatThrownBy(() -> cleanupTask.cleanupExpiredTokens());

      // 3. Assert
      throwableAssert
              .isInstanceOf(IllegalStateException.class)
              .hasMessage("Falha ao remover refresh tokens expirados.");

      assertThat(cutoffDateCaptor.getValue()).isNotNull();
      verify(refreshTokenJpaRepository).deleteExpiredTokensBefore(cutoffDateCaptor.getValue());
      verifyNoMoreInteractions(refreshTokenJpaRepository);
    }
  }
}
