package com.app.url_shortener.auth.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class PermissionNameRequiredException extends DomainValidationException {

  public PermissionNameRequiredException() {
    super(AuthErrorCode.AUTH_PERMISSION_NAME_REQUIRED);
  }
}
