package com.app.url_shortener.iam.domain.exception.auth;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class RefreshTokenExpiredException extends DomainValidationException {

  public RefreshTokenExpiredException() {
    super(AuthErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
  }
}
