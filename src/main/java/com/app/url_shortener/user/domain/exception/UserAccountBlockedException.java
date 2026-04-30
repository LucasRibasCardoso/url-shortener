package com.app.url_shortener.user.domain.exception;

import com.app.url_shortener.shared.exception.conflict.ConflictException;

public class UserAccountBlockedException extends ConflictException {

  public UserAccountBlockedException() {
    super(UserErrorCode.USER_ACCOUNT_BLOCKED);
  }
}
