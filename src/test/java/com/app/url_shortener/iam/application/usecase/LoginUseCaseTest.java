package com.app.url_shortener.iam.application.usecase;

import com.app.url_shortener.iam.application.command.LoginCommand;
import com.app.url_shortener.iam.application.port.output.AuthenticateCredentialsPort;
import com.app.url_shortener.iam.application.port.output.IssueAccessTokenPort;
import com.app.url_shortener.iam.application.port.output.RefreshTokenRepositoryPort;
import com.app.url_shortener.iam.application.port.output.SecureTokenGeneratorPort;
import com.app.url_shortener.iam.application.result.AuthenticatedUserResult;
import com.app.url_shortener.iam.application.usecase.impl.LoginUseCaseImpl;
import com.app.url_shortener.iam.domain.exception.auth.InvalidCredentialsException;
import com.app.url_shortener.iam.domain.model.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Caso de Uso Login")
class LoginUseCaseTest {

  @Mock
  private IssueAccessTokenPort issueAccessTokenPort;

  @Mock
  private AuthenticateCredentialsPort authenticateCredentialsPort;

  @Mock
  private SecureTokenGeneratorPort tokenGenerator;

  @Mock
  private RefreshTokenRepositoryPort tokenRepository;

  @Captor
  private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

  @InjectMocks
  private LoginUseCaseImpl loginUseCase;

  @Nested
  @DisplayName("Execução do Login")
  class ExecuteTests {

    @Test
    @DisplayName("Deve autenticar credenciais, emitir tokens e salvar refresh token com sucesso")
    void shouldAuthenticateCredentialsIssueTokensAndSaveRefreshTokenSuccessfully() {
      // 1. Arrange
      var command = new LoginCommand(" USER@EMAIL.COM ", "secure-password");
      var authenticatedUser = authenticatedUser();
      var rawRefreshToken = "raw-refresh-token";
      var refreshTokenHash = "hashed-refresh-token";
      var accessToken = "jwt-access-token";
      var expiresInSeconds = 3_600L;

      given(authenticateCredentialsPort.authenticate("user@email.com", command.password())).willReturn(authenticatedUser);
      given(issueAccessTokenPort.getToken(authenticatedUser)).willReturn(accessToken);
      given(tokenGenerator.generateRandomToken()).willReturn(rawRefreshToken);
      given(tokenGenerator.hashToken(rawRefreshToken)).willReturn(refreshTokenHash);
      given(issueAccessTokenPort.getExpiresInSeconds()).willReturn(expiresInSeconds);

      // 2. Act
      var result = loginUseCase.execute(command);

      // 3. Assert
      assertAll(
              () -> assertThat(result.refreshToken()).isEqualTo(rawRefreshToken),
              () -> assertThat(result.accessToken()).isEqualTo(accessToken),
              () -> assertThat(result.tokenType()).isEqualTo("Bearer"),
              () -> assertThat(result.expiresInSeconds()).isEqualTo(expiresInSeconds),
              () -> assertThat(result.user()).isEqualTo(authenticatedUser)
      );

      verify(tokenRepository).save(refreshTokenCaptor.capture());
      var savedRefreshToken = refreshTokenCaptor.getValue();

      assertAll(
              () -> assertThat(savedRefreshToken.getId()).isNotNull(),
              () -> assertThat(savedRefreshToken.getUserId()).isEqualTo(authenticatedUser.id()),
              () -> assertThat(savedRefreshToken.getTokenHash()).isEqualTo(refreshTokenHash),
              () -> assertThat(savedRefreshToken.isRevoked()).isFalse(),
              () -> assertThat(savedRefreshToken.getCreatedAt()).isNotNull(),
              () -> assertThat(savedRefreshToken.getExpiresAt()).isAfter(savedRefreshToken.getCreatedAt())
      );

      verify(authenticateCredentialsPort).authenticate("user@email.com", command.password());
      verify(issueAccessTokenPort).getToken(authenticatedUser);
      verify(tokenGenerator).generateRandomToken();
      verify(tokenGenerator).hashToken(rawRefreshToken);
      verify(issueAccessTokenPort).getExpiresInSeconds();
      verifyNoMoreInteractions(
              issueAccessTokenPort,
              authenticateCredentialsPort,
              tokenGenerator,
              tokenRepository
      );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("Deve rejeitar login quando o email for nulo, vazio ou em branco")
    void shouldRejectLoginWhenEmailIsNullEmptyOrBlank(String email) {
      // 1. Arrange
      var command = new LoginCommand(email, "secure-password");

      // 2. Act
      var throwableAssert = assertThatThrownBy(() -> loginUseCase.execute(command));

      // 3. Assert
      throwableAssert
              .isInstanceOf(InvalidCredentialsException.class)
              .hasMessage("Credenciais inválidas.");

      verifyNoInteractions(
              issueAccessTokenPort,
              authenticateCredentialsPort,
              tokenGenerator,
              tokenRepository
      );
    }

    @Test
    @DisplayName("Deve propagar exceção quando as credenciais forem inválidas")
    void shouldPropagateExceptionWhenCredentialsAreInvalid() {
      // 1. Arrange
      var command = new LoginCommand("user@email.com", "wrong-password");
      var exception = new InvalidCredentialsException();

      given(authenticateCredentialsPort.authenticate(command.email(), command.password()))
              .willThrow(exception);

      // 2. Act
      var throwableAssert = assertThatThrownBy(() -> loginUseCase.execute(command));

      // 3. Assert
      throwableAssert
              .isInstanceOf(InvalidCredentialsException.class)
              .hasMessage("Credenciais inválidas.");

      verify(authenticateCredentialsPort).authenticate(command.email(), command.password());
      verifyNoInteractions(issueAccessTokenPort, tokenGenerator, tokenRepository);
      verifyNoMoreInteractions(authenticateCredentialsPort);
    }
  }

  private AuthenticatedUserResult authenticatedUser() {
    return new AuthenticatedUserResult(
            UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123"),
            "User Name",
            "user@email.com",
            List.of("USER"),
            List.of("url:create", "url:read"),
            "FREE"
    );
  }
}
