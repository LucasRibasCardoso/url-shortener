package com.app.url_shortener.iam.presentation.controller;

import com.app.url_shortener.iam.application.command.LoginCommand;
import com.app.url_shortener.iam.application.command.RefreshTokenCommand;
import com.app.url_shortener.iam.application.command.RegisterUserCommand;
import com.app.url_shortener.iam.application.command.VerifyEmailCommand;
import com.app.url_shortener.iam.application.result.LoginResult;
import com.app.url_shortener.iam.application.result.RefreshTokenResult;
import com.app.url_shortener.iam.application.result.RegisterUserResult;
import com.app.url_shortener.iam.application.result.VerifyEmailResult;
import com.app.url_shortener.iam.application.usecase.LoginUseCase;
import com.app.url_shortener.iam.application.usecase.RefreshTokenUseCase;
import com.app.url_shortener.iam.application.usecase.RegisterUserUseCase;
import com.app.url_shortener.iam.application.usecase.VerifyEmailUseCase;
import com.app.url_shortener.iam.domain.exception.auth.InvalidRefreshTokenException;
import com.app.url_shortener.iam.presentation.dto.request.LoginRequestDto;
import com.app.url_shortener.iam.presentation.dto.request.RegisterRequestDto;
import com.app.url_shortener.iam.presentation.dto.request.VerifyEmailRequestDto;
import com.app.url_shortener.iam.presentation.dto.response.LoginResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.RefreshTokenResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.RegisterResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.VerifyEmailResponseDto;
import com.app.url_shortener.iam.presentation.mapper.IamWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  public static final Duration DEFAULT_MAX_AGE = Duration.ofDays(7);
  private final IamWebMapper iamWebMapper;
  private final LoginUseCase loginUseCase;
  private final VerifyEmailUseCase verifyEmailUseCase;
  private final RegisterUserUseCase registerUserUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
    RegisterUserCommand command = iamWebMapper.toCommand(request);
    RegisterUserResult result = registerUserUseCase.execute(command);
    RegisterResponseDto response = iamWebMapper.toResponse(result);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/verify-email")
  public ResponseEntity<VerifyEmailResponseDto> verifyEmail(@Valid @RequestBody VerifyEmailRequestDto request) {
    VerifyEmailCommand command = iamWebMapper.toCommand(request);
    VerifyEmailResult result = verifyEmailUseCase.execute(command);
    VerifyEmailResponseDto response = iamWebMapper.toResponse(result);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
    LoginCommand command = iamWebMapper.toCommand(request);
    LoginResult result = loginUseCase.execute(command);
    LoginResponseDto response = iamWebMapper.toResponse(result);

    ResponseCookie cookie = buildRefreshTokenCookie(result.refreshToken(), "/api/v1/auth", DEFAULT_MAX_AGE);
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<RefreshTokenResponseDto> refresh(@CookieValue(required = false) String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) throw new InvalidRefreshTokenException();

    RefreshTokenResult result = refreshTokenUseCase.execute(new RefreshTokenCommand(refreshToken));
    RefreshTokenResponseDto response = iamWebMapper.toResponse(result);

    ResponseCookie cookie = buildRefreshTokenCookie(result.newRefreshToken(), "/api/v1/auth", DEFAULT_MAX_AGE);
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
  }

  private ResponseCookie buildRefreshTokenCookie(String value, String path, Duration maxAge) {
    return ResponseCookie.from("refreshToken", value)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path(path)
            .maxAge(maxAge)
            .build();
  }
}
