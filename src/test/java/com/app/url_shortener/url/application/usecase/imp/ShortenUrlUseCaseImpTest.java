package com.app.url_shortener.url.application.usecase.imp;

import com.app.url_shortener.url.application.port.output.IdGeneratorPort;
import com.app.url_shortener.url.application.port.output.UrlEncoderPort;
import com.app.url_shortener.url.application.port.output.UrlRepositoryPort;
import com.app.url_shortener.url.domain.model.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Caso de Uso de Encurtamento de URL")
class ShortenUrlUseCaseImpTest {

  @Mock
  private IdGeneratorPort idGeneratorService;

  @Mock
  private UrlEncoderPort urlEncoderPort;

  @Mock
  private UrlRepositoryPort urlRepositoryPort;

  @InjectMocks
  private ShortenUrlUseCaseImp shortenUrlUseCaseImp;

  @Nested
  @DisplayName("Execução")
  class ExecuteTests {

    @Test
    @DisplayName("Deve gerar código curto, persistir e retornar a URL encurtada")
    void shouldGenerateShortCodeSaveAndReturnShortenedUrl() {
      // 1. Arrange
      var generatedId = 100L;
      var originalUrl = "https://google.com";
      var shortCode = "aB3dE";
      when(idGeneratorService.generateId()).thenReturn(generatedId);
      when(urlEncoderPort.encode(generatedId)).thenReturn(shortCode);

      // 2. Act
      var result = shortenUrlUseCaseImp.execute(originalUrl);

      // 3. Assert
      assertThat(result.getShortCode()).isEqualTo(shortCode);
      assertThat(result.getOriginalUrl()).isEqualTo(originalUrl);
      assertThat(result.getCreatedAt()).isNotNull();

      var urlCaptor = ArgumentCaptor.forClass(Url.class);
      verify(idGeneratorService).generateId();
      verify(urlEncoderPort).encode(generatedId);
      verify(urlRepositoryPort).save(urlCaptor.capture());

      var capturedUrl = urlCaptor.getValue();
      assertThat(capturedUrl).isSameAs(result);
      assertThat(capturedUrl.getShortCode()).isEqualTo(shortCode);
      assertThat(capturedUrl.getOriginalUrl()).isEqualTo(originalUrl);
      assertThat(capturedUrl.getCreatedAt()).isNotNull();

      verifyNoMoreInteractions(idGeneratorService, urlEncoderPort, urlRepositoryPort);
    }
  }
}
