package com.app.url_shortener.iam.domain.exception.auth;

import com.app.url_shortener.shared.exception.unauthorized.UnauthorizedException;

public class RefreshTokenExpiredException extends UnauthorizedException {

  public RefreshTokenExpiredException() {
    super(AuthErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
  }
}
