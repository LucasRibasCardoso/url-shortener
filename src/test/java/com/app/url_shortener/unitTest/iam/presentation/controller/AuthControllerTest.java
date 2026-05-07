package com.app.url_shortener.unitTest.iam.presentation.controller;

import com.app.url_shortener.iam.application.command.LoginCommand;
import com.app.url_shortener.iam.application.command.LogoutCommand;
import com.app.url_shortener.iam.application.command.RefreshTokenCommand;
import com.app.url_shortener.iam.application.command.RegisterUserCommand;
import com.app.url_shortener.iam.application.command.ResendVerificationCommand;
import com.app.url_shortener.iam.application.command.VerifyEmailCommand;
import com.app.url_shortener.iam.application.result.AuthenticatedUserResult;
import com.app.url_shortener.iam.application.result.LoginResult;
import com.app.url_shortener.iam.application.result.RefreshTokenResult;
import com.app.url_shortener.iam.application.result.RegisterUserResult;
import com.app.url_shortener.iam.application.result.ResendVerificationResult;
import com.app.url_shortener.iam.application.result.VerifyEmailResult;
import com.app.url_shortener.iam.application.usecase.LoginUseCase;
import com.app.url_shortener.iam.application.usecase.LogoutUseCase;
import com.app.url_shortener.iam.application.usecase.RefreshTokenUseCase;
import com.app.url_shortener.iam.application.usecase.RegisterUserUseCase;
import com.app.url_shortener.iam.application.usecase.ResendVerificationUseCase;
import com.app.url_shortener.iam.application.usecase.VerifyEmailUseCase;
import com.app.url_shortener.iam.domain.exception.auth.InvalidRefreshTokenException;
import com.app.url_shortener.iam.domain.valueobject.VerificationCode;
import com.app.url_shortener.iam.presentation.controller.AuthController;
import com.app.url_shortener.iam.presentation.dto.request.LoginRequestDto;
import com.app.url_shortener.iam.presentation.dto.request.RegisterRequestDto;
import com.app.url_shortener.iam.presentation.dto.request.ResendVerificationRequest;
import com.app.url_shortener.iam.presentation.dto.request.VerifyEmailRequestDto;
import com.app.url_shortener.iam.presentation.dto.response.AuthenticatedUserDto;
import com.app.url_shortener.iam.presentation.dto.response.GenericMessageResponse;
import com.app.url_shortener.iam.presentation.dto.response.LoginResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.RefreshTokenResponseDto;
import com.app.url_shortener.iam.presentation.mapper.IamWebMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - AuthController")
class AuthControllerTest {

  @Mock
  private IamWebMapper iamWebMapper;

  @Mock
  private LoginUseCase loginUseCase;

  @Mock
  private LogoutUseCase logoutUseCase;

  @Mock
  private VerifyEmailUseCase verifyEmailUseCase;

  @Mock
  private RegisterUserUseCase registerUserUseCase;

  @Mock
  private RefreshTokenUseCase refreshTokenUseCase;

  @Mock
  private ResendVerificationUseCase resendVerificationUseCase;

  @InjectMocks
  private AuthController controller;

  @Nested
  @DisplayName("Cadastro")
  class RegisterTests {

    @Test
    @DisplayName("Deve registrar usuário e retornar status 201")
    void shouldRegisterUserAndReturnCreatedStatus() {
      // 1. Arrange
      var request = new RegisterRequestDto("User Name", "user@email.com", "secure-password");
      var command = new RegisterUserCommand(request.name(), request.email(), request.password());
      var result = new RegisterUserResult("Usuário cadastrado com sucesso.");
      var response = new GenericMessageResponse(result.message());

      given(iamWebMapper.toCommand(request)).willReturn(command);
      given(registerUserUseCase.execute(command)).willReturn(result);
      given(iamWebMapper.toResponse(result)).willReturn(response);

      // 2. Act
      var responseEntity = controller.register(request);

      // 3. Assert
      assertAll(
              () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED),
              () -> assertThat(responseEntity.getBody()).isEqualTo(response)
      );

      verify(iamWebMapper).toCommand(request);
      verify(registerUserUseCase).execute(command);
      verify(iamWebMapper).toResponse(result);
      verifyNoMoreInteractions(iamWebMapper, registerUserUseCase);
      verifyNoInteractions(
              loginUseCase,
              logoutUseCase,
              verifyEmailUseCase,
              refreshTokenUseCase,
              resendVerificationUseCase
      );
    }
  }

  @Nested
  @DisplayName("Verificação de Email")
  class VerifyEmailTests {

    @Test
    @DisplayName("Deve verificar email e retornar status 200")
    void shouldVerifyEmailAndReturnOkStatus() {
      // 1. Arrange
      var request = new VerifyEmailRequestDto("user@email.com", "123456");
      var command = new VerifyEmailCommand(request.email(), VerificationCode.of(request.code()));
      var result = new VerifyEmailResult("Email verificado com sucesso.");
      var response = new GenericMessageResponse(result.message());

      given(iamWebMapper.toCommand(request)).willReturn(command);
      given(verifyEmailUseCase.execute(command)).willReturn(result);
      given(iamWebMapper.toResponse(result)).willReturn(response);

      // 2. Act
      var responseEntity = controller.verifyEmail(request);

      // 3. Assert
      assertAll(
              () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK),
              () -> assertThat(responseEntity.getBody()).isEqualTo(response)
      );

      verify(iamWebMapper).toCommand(request);
      verify(verifyEmailUseCase).execute(command);
      verify(iamWebMapper).toResponse(result);
      verifyNoMoreInteractions(iamWebMapper, verifyEmailUseCase);
      verifyNoInteractions(
              loginUseCase,
              logoutUseCase,
              registerUserUseCase,
              refreshTokenUseCase,
              resendVerificationUseCase
      );
    }
  }

  @Nested
  @DisplayName("Login")
  class LoginTests {

    @Test
    @DisplayName("Deve autenticar usuário, retornar resposta e configurar cookie de refresh token")
    void shouldAuthenticateUserReturnResponseAndSetRefreshTokenCookie() {
      // 1. Arrange
      var request = new LoginRequestDto("user@email.com", "secure-password");
      var command = new LoginCommand(request.email(), request.password());
      var result = loginResult("raw-refresh-token", "jwt-access-token");
      var response = loginResponseDto();

      given(iamWebMapper.toCommand(request)).willReturn(command);
      given(loginUseCase.execute(command)).willReturn(result);
      given(iamWebMapper.toResponse(result)).willReturn(response);

      // 2. Act
      var responseEntity = controller.login(request);

      // 3. Assert
      var cookie = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

      assertAll(
              () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK),
              () -> assertThat(responseEntity.getBody()).isEqualTo(response),
              () -> assertThat(cookie).contains("refreshToken=raw-refresh-token"),
              () -> assertThat(cookie).contains("Path=/api/v1/auth"),
              () -> assertThat(cookie).contains("Max-Age=604800"),
              () -> assertThat(cookie).contains("Secure"),
              () -> assertThat(cookie).contains("HttpOnly"),
              () -> assertThat(cookie).contains("SameSite=Strict")
      );

      verify(iamWebMapper).toCommand(request);
      verify(loginUseCase).execute(command);
      verify(iamWebMapper).toResponse(result);
      verifyNoMoreInteractions(iamWebMapper, loginUseCase);
      verifyNoInteractions(
              logoutUseCase,
              verifyEmailUseCase,
              registerUserUseCase,
              refreshTokenUseCase,
              resendVerificationUseCase
      );
    }
  }

  @Nested
  @DisplayName("Refresh Token")
  class RefreshTests {

    @Test
    @DisplayName("Deve renovar token e configurar novo cookie de refresh token")
    void shouldRefreshTokenAndSetNewRefreshTokenCookie() {
      // 1. Arrange
      var refreshToken = "current-refresh-token";
      var result = new RefreshTokenResult("new-refresh-token", "new-access-token");
      var response = new RefreshTokenResponseDto(result.newAccessToken());

      given(refreshTokenUseCase.execute(new RefreshTokenCommand(refreshToken))).willReturn(result);
      given(iamWebMapper.toResponse(result)).willReturn(response);

      // 2. Act
      var responseEntity = controller.refresh(refreshToken);

      // 3. Assert
      var cookie = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

      assertAll(
              () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK),
              () -> assertThat(responseEntity.getBody()).isEqualTo(response),
              () -> assertThat(cookie).contains("refreshToken=new-refresh-token"),
              () -> assertThat(cookie).contains("Path=/api/v1/auth"),
              () -> assertThat(cookie).contains("Max-Age=604800"),
              () -> assertThat(cookie).contains("Secure"),
              () -> assertThat(cookie).contains("HttpOnly"),
              () -> assertThat(cookie).contains("SameSite=Strict")
      );

      verify(refreshTokenUseCase).execute(new RefreshTokenCommand(refreshToken));
      verify(iamWebMapper).toResponse(result);
      verifyNoMoreInteractions(refreshTokenUseCase, iamWebMapper);
      verifyNoInteractions(
              loginUseCase,
              logoutUseCase,
              verifyEmailUseCase,
              registerUserUseCase,
              resendVerificationUseCase
      );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("Deve rejeitar refresh quando cookie estiver ausente ou em branco")
    void shouldRejectRefreshWhenCookieIsMissingOrBlank(String refreshToken) {
      // 1. Arrange

      // 2. Act
      var throwableAssert = assertThatThrownBy(() -> controller.refresh(refreshToken));

      // 3. Assert
      throwableAssert
              .isInstanceOf(InvalidRefreshTokenException.class)
              .hasMessage("Refresh token inválido.");

      verifyNoInteractions(
              iamWebMapper,
              loginUseCase,
              logoutUseCase,
              verifyEmailUseCase,
              registerUserUseCase,
              refreshTokenUseCase,
              resendVerificationUseCase
      );
    }
  }

  @Nested
  @DisplayName("Logout")
  class LogoutTests {

    @Test
    @DisplayName("Deve executar logout e expirar cookie de refresh token")
    void shouldLogoutAndExpireRefreshTokenCookie() {
      // 1. Arrange
      var refreshToken = "raw-refresh-token";

      // 2. Act
      var responseEntity = controller.logout(refreshToken);

      // 3. Assert
      var cookie = responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

      assertAll(
              () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT),
              () -> assertThat(responseEntity.getBody()).isNull(),
              () -> assertThat(cookie).contains("refreshToken="),
              () -> assertThat(cookie).contains("Path=/api/v1/auth"),
              () -> assertThat(cookie).contains("Max-Age=0"),
              () -> assertThat(cookie).contains("Secure"),
              () -> assertThat(cookie).contains("HttpOnly"),
              () -> assertThat(cookie).contains("SameSite=Strict")
      );

      verify(logoutUseCase).execute(new LogoutCommand(refreshToken));
      verifyNoMoreInteractions(logoutUseCase);
      verifyNoInteractions(
              iamWebMapper,
              loginUseCase,
              verifyEmailUseCase,
              registerUserUseCase,
              refreshTokenUseCase,
              resendVerificationUseCase
      );
    }
  }

  @Nested
  @DisplayName("Reenvio de Verificação")
  class ResendTests {

    @Test
    @DisplayName("Deve reenviar verificação e retornar status 200")
    void shouldResendVerificationAndReturnOkStatus() {
      // 1. Arrange
      var request = new ResendVerificationRequest("user@email.com");
      var command = new ResendVerificationCommand(request.email());
      var result = new ResendVerificationResult("Código reenviado com sucesso.");
      var response = new GenericMessageResponse(result.message());

      given(iamWebMapper.toCommand(request)).willReturn(command);
      given(resendVerificationUseCase.execute(command)).willReturn(result);
      given(iamWebMapper.toResponse(result)).willReturn(response);

      // 2. Act
      var responseEntity = controller.resend(request);

      // 3. Assert
      assertAll(
              () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK),
              () -> assertThat(responseEntity.getBody()).isEqualTo(response)
      );

      verify(iamWebMapper).toCommand(request);
      verify(resendVerificationUseCase).execute(command);
      verify(iamWebMapper).toResponse(result);
      verifyNoMoreInteractions(iamWebMapper, resendVerificationUseCase);
      verifyNoInteractions(
              loginUseCase,
              logoutUseCase,
              verifyEmailUseCase,
              registerUserUseCase,
              refreshTokenUseCase
      );
    }
  }

  private LoginResult loginResult(String refreshToken, String accessToken) {
    return new LoginResult(
            refreshToken,
            accessToken,
            "Bearer",
            3_600L,
            new AuthenticatedUserResult(
                    UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123"),
                    "User Name",
                    "user@email.com",
                    List.of("USER"),
                    List.of("ROLE_USER"),
                    "FREE"
            )
    );
  }

  private LoginResponseDto loginResponseDto() {
    return new LoginResponseDto(
            "jwt-access-token",
            "Bearer",
            3_600L,
            new AuthenticatedUserDto(
                    UUID.fromString("019a16f1-ae7f-7c9d-9e18-44773f1ac123"),
                    "User Name",
                    "user@email.com",
                    "FREE",
                    List.of("USER")
            )
    );
  }
}
