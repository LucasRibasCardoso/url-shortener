package com.app.url_shortener.iam.domain.exception.validation;

import com.app.url_shortener.iam.domain.exception.auth.AuthErrorCode;
import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class EmailRequiredException extends DomainValidationException {

  public EmailRequiredException() {
    super(AuthErrorCode.AUTH_EMAIL_REQUIRED);
  }
}
