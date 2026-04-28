package com.app.url_shortener.auth.application.service;

import com.app.url_shortener.auth.presentation.dto.request.RegisterRequest;
import com.app.url_shortener.auth.presentation.dto.response.AuthResponse;

public interface AuthService {
  AuthResponse register(RegisterRequest registerRequest);
}
