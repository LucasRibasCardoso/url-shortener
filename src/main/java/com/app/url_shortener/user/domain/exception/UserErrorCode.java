package com.app.url_shortener.user.domain.exception;

import com.app.url_shortener.shared.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
  USER_PLAN_INVALID("Plano de usuario invalido."),
  USER_EMAIL_ALREADY_EXISTS("E-mail existente."),
  USER_ACCOUNT_BLOCKED("Conta bloqueada."),
  ;

  private final String message;

  UserErrorCode(String message) {
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
