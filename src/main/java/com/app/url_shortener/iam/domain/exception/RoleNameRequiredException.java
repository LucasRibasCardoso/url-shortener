package com.app.url_shortener.iam.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class RoleNameRequiredException extends DomainValidationException {

  public RoleNameRequiredException() {
    super(AuthErrorCode.AUTH_ROLE_NAME_REQUIRED);
  }
}
