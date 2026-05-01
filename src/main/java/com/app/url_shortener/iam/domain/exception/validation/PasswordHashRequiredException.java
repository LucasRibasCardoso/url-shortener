package com.app.url_shortener.iam.domain.exception.validation;

import com.app.url_shortener.iam.domain.exception.auth.AuthErrorCode;
import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class PasswordHashRequiredException extends DomainValidationException {

  public PasswordHashRequiredException() {
    super(AuthErrorCode.AUTH_PASSWORD_HASH_REQUIRED);
  }
}
