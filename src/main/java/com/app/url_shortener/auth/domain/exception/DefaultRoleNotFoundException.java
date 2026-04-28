package com.app.url_shortener.auth.domain.exception;

import com.app.url_shortener.shared.exception.notfound.NotFoundException;

public class DefaultRoleNotFoundException extends NotFoundException {

  public DefaultRoleNotFoundException() {
    super(AuthErrorCode.AUTH_DEFAULT_ROLE_NOT_FOUND);
  }

}
