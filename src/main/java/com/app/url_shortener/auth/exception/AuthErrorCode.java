package com.app.url_shortener.auth.exception;

import com.app.url_shortener.shared.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

  AUTH_INVALID_CREDENTIALS("Credenciais inválidas."),
  AUTH_REFRESH_TOKEN_INVALID("Refresh token inválido."),
  AUTH_REFRESH_TOKEN_EXPIRED("Refresh token expirado."),
  AUTH_REFRESH_TOKEN_REVOKED("Refresh token revogado."),
  AUTH_EMAIL_ALREADY_EXISTS("Email já cadastrado."),
  AUTH_PASSWORD_RESET_TOKEN_INVALID("Token de redefinição de senha inválido."),
  AUTH_PASSWORD_RESET_TOKEN_EXPIRED("Token de redefinição de senha expirado.");

  private final String message;

  AuthErrorCode(String message) {
    this.message = message;
  }

  @Override
  public String getCode() {
    return name();
  }

  @Override
  public String getMessage() {
    return message;
  }
}