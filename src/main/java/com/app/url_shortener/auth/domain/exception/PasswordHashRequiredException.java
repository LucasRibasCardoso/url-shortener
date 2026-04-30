package com.app.url_shortener.auth.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class PasswordHashRequiredException extends DomainValidationException {

  public PasswordHashRequiredException() {
    super(AuthErrorCode.AUTH_PASSWORD_HASH_REQUIRED);
  }
}
