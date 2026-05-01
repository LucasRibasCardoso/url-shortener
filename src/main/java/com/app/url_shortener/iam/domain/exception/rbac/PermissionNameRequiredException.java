package com.app.url_shortener.iam.domain.exception.rbac;

import com.app.url_shortener.iam.domain.exception.auth.AuthErrorCode;
import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class PermissionNameRequiredException extends DomainValidationException {

  public PermissionNameRequiredException() {
    super(AuthErrorCode.AUTH_PERMISSION_NAME_REQUIRED);
  }
}
