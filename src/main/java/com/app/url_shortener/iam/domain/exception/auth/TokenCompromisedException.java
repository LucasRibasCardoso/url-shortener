package com.app.url_shortener.iam.domain.exception.auth;

import com.app.url_shortener.shared.exception.forbidden.ForbiddenException;

public class TokenCompromisedException extends ForbiddenException {

  public TokenCompromisedException() {
    super(AuthErrorCode.AUTH_REFRESH_TOKEN_COMPROMISED);
  }
}
