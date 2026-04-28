package com.app.url_shortener.auth.presentation.controller;

import com.app.url_shortener.auth.application.service.AuthService;
import com.app.url_shortener.auth.presentation.dto.request.RegisterRequest;
import com.app.url_shortener.auth.presentation.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    AuthResponse response = authService.register(request);

    URI location = UriComponentsBuilder
            .fromPath("/api/v1/users/{id}")
            .buildAndExpand(response.user().id())
            .toUri();

    return ResponseEntity.created(location).body(response);
  }

  // TODO: Endpoint para Login
  // TODO: Endpoint para Refresh Token
  // TODO: Endpoint para Logout
}
