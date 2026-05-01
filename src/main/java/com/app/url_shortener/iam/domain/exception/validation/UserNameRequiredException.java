package com.app.url_shortener.iam.domain.exception.validation;

import com.app.url_shortener.iam.domain.exception.auth.AuthErrorCode;
import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class UserNameRequiredException extends DomainValidationException {

  public UserNameRequiredException() {
    super(AuthErrorCode.AUTH_USER_NAME_REQUIRED);
  }
}
