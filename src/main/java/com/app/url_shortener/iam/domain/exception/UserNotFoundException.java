package com.app.url_shortener.iam.domain.exception;

import com.app.url_shortener.shared.exception.notfound.NotFoundException;

public class UserNotFoundException extends NotFoundException {

  public UserNotFoundException() {
    super(AuthErrorCode.AUTH_USER_NOT_FOUND);
  }
}
