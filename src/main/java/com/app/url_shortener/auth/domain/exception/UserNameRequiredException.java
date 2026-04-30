package com.app.url_shortener.auth.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class UserNameRequiredException extends DomainValidationException {

  public UserNameRequiredException() {
    super(AuthErrorCode.AUTH_USER_NAME_REQUIRED);
  }
}
