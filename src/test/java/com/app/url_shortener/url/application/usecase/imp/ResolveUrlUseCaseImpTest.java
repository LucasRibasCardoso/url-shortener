package com.app.url_shortener.url.application.usecase.imp;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Caso de Uso de Resolução de URL")
class ResolveUrlUseCaseImpTest {

  @Mock
  private UrlRepositoryPort urlRepositoryPort;

  @InjectMocks
  private ResolveUrlUseCaseImp resolveUrlUseCaseImp;

  @Nested
  @DisplayName("Execução")
  class ExecuteTests {

    @Test
    @DisplayName("Deve retornar a URL original quando o código curto existir")
    void shouldReturnOriginalUrlWhenShortCodeExists() {
      // 1. Arrange
      var shortCode = "aB3dE";
      var originalUrl = "https://google.com";
      var url = Url.create(shortCode, originalUrl);
      when(urlRepositoryPort.findByShortCode(shortCode)).thenReturn(Optional.of(url));

      // 2. Act
      var result = resolveUrlUseCaseImp.execute(shortCode);

      // 3. Assert
      assertThat(result).isEqualTo(originalUrl);
      verify(urlRepositoryPort).findByShortCode(shortCode);
      verifyNoMoreInteractions(urlRepositoryPort);
    }

    @Test
    @DisplayName("Deve lançar UrlNotFoundException quando o código curto não existir")
    void shouldThrowUrlNotFoundExceptionWhenShortCodeDoesNotExist() {
      // 1. Arrange
      var shortCode = "invalid";
      when(urlRepositoryPort.findByShortCode(shortCode)).thenReturn(Optional.empty());

      // 2. Act & 3. Assert
      assertThatThrownBy(() -> resolveUrlUseCaseImp.execute(shortCode))
          .isInstanceOf(UrlNotFoundException.class);
      verify(urlRepositoryPort).findByShortCode(shortCode);
      verifyNoMoreInteractions(urlRepositoryPort);
    }
  }
}
