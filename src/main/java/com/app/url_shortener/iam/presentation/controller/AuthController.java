package com.app.url_shortener.iam.presentation.controller;

import com.app.url_shortener.iam.application.command.LoginCommand;
import com.app.url_shortener.iam.application.command.RegisterUserCommand;
import com.app.url_shortener.iam.application.command.VerifyEmailCommand;
import com.app.url_shortener.iam.application.result.LoginResult;
import com.app.url_shortener.iam.application.result.RegisterUserResult;
import com.app.url_shortener.iam.application.result.VerifyEmailResult;
import com.app.url_shortener.iam.application.usecase.LoginUseCase;
import com.app.url_shortener.iam.application.usecase.RegisterUserUseCase;
import com.app.url_shortener.iam.application.usecase.VerifyEmailUseCase;
import com.app.url_shortener.iam.presentation.dto.request.LoginRequestDto;
import com.app.url_shortener.iam.presentation.dto.request.RegisterRequestDto;
import com.app.url_shortener.iam.presentation.dto.request.VerifyEmailRequestDto;
import com.app.url_shortener.iam.presentation.dto.response.LoginResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.RegisterResponseDto;
import com.app.url_shortener.iam.presentation.dto.response.VerifyEmailResponseDto;
import com.app.url_shortener.iam.presentation.mapper.IamWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final IamWebMapper iamWebMapper;
  private final LoginUseCase loginUseCase;
  private final VerifyEmailUseCase verifyEmailUseCase;
  private final RegisterUserUseCase registerUserUseCase;

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

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
  // TODO: Endpoint para Refresh Token
  // TODO: Endpoint para Logout
}
