package com.app.url_shortener.auth.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class EmailRequiredException extends DomainValidationException {

  public EmailRequiredException() {
    super(AuthErrorCode.AUTH_EMAIL_REQUIRED);
  }
}
