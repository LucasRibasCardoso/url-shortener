package com.app.url_shortener.iam.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class InvalidCredentialsException extends DomainValidationException {

  public InvalidCredentialsException() {
    super(AuthErrorCode.AUTH_INVALID_CREDENTIALS);
  }
}
