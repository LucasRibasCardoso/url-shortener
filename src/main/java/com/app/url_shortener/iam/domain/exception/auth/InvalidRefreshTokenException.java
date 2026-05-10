package com.app.url_shortener.iam.domain.exception.auth;

import com.app.url_shortener.shared.exception.unauthorized.UnauthorizedException;

public class InvalidRefreshTokenException extends UnauthorizedException {

  public InvalidRefreshTokenException() {
    super(AuthErrorCode.AUTH_REFRESH_TOKEN_INVALID);
  }
}
