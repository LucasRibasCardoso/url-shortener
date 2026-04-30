package com.app.url_shortener.iam.presentation.controller;

import com.app.url_shortener.iam.application.command.RegisterUserCommand;
import com.app.url_shortener.iam.application.command.VerifyEmailCommand;
import com.app.url_shortener.iam.application.result.RegisterUserResult;
import com.app.url_shortener.iam.application.result.VerifyEmailResult;
import com.app.url_shortener.iam.application.usecase.RegisterUserUseCase;
import com.app.url_shortener.iam.application.usecase.VerifyEmailUseCase;
import com.app.url_shortener.iam.presentation.dto.request.RegisterRequest;
import com.app.url_shortener.iam.presentation.dto.request.VerifyEmailRequest;
import com.app.url_shortener.iam.presentation.dto.response.RegisterResponse;
import com.app.url_shortener.iam.presentation.dto.response.VerifyEmailResponse;
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
  private final VerifyEmailUseCase verifyEmailUseCase;
  private final RegisterUserUseCase registerUserUseCase;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
    RegisterUserCommand command = iamWebMapper.toCommand(request);
    RegisterUserResult result = registerUserUseCase.execute(command);
    RegisterResponse response = iamWebMapper.toResponse(result);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/verify-email")
  public ResponseEntity<VerifyEmailResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
    VerifyEmailCommand command = iamWebMapper.toCommand(request);
    VerifyEmailResult result = verifyEmailUseCase.execute(command);
    VerifyEmailResponse response = iamWebMapper.toResponse(result);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // TODO: Endpoint para Login
  // TODO: Endpoint para Refresh Token
  // TODO: Endpoint para Logout
}
