package com.app.url_shortener.url.application.usecase.impl;

import com.app.url_shortener.url.application.command.ResolveUrlCommand;
import com.app.url_shortener.url.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.url.domain.exception.UrlNotFoundException;
import com.app.url_shortener.url.domain.model.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Caso de Uso de Resolução de URL")
class ResolveUrlUseCaseImplTest {

  @Mock
  private UrlRepositoryPort urlRepositoryPort;

  @InjectMocks
  private ResolveUrlUseCaseImpl resolveUrlUseCase;

  @Nested
  @DisplayName("Execução")
  class ExecuteTests {

    @Test
    @DisplayName("Deve retornar a URL original quando o código curto existir")
    void shouldReturnOriginalUrlWhenShortCodeExists() {
      // 1. Arrange
      var userId = UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac001");
      var shortCode = "aB3dE";
      var command = new ResolveUrlCommand(shortCode);
      var originalUrl = "https://google.com";
      var url = Url.create(userId, shortCode, originalUrl);
      when(urlRepositoryPort.findByShortCode(shortCode)).thenReturn(Optional.of(url));

      // 2. Act
      var result = resolveUrlUseCase.execute(command);

      // 3. Assert
      assertThat(result.originalUrl()).isEqualTo(originalUrl);
      verify(urlRepositoryPort).findByShortCode(shortCode);
      verifyNoMoreInteractions(urlRepositoryPort);
    }

    @Test
    @DisplayName("Deve lançar UrlNotFoundException quando o código curto não existir")
    void shouldThrowUrlNotFoundExceptionWhenShortCodeDoesNotExist() {
      // 1. Arrange
      var shortCode = "invalid";
      var command = new ResolveUrlCommand(shortCode);
      when(urlRepositoryPort.findByShortCode(shortCode)).thenReturn(Optional.empty());

      // 2. Act & 3. Assert
      assertThatThrownBy(() -> resolveUrlUseCase.execute(command))
          .isInstanceOf(UrlNotFoundException.class);
      verify(urlRepositoryPort).findByShortCode(shortCode);
      verifyNoMoreInteractions(urlRepositoryPort);
    }
  }
}
