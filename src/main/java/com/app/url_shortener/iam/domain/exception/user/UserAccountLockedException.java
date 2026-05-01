package com.app.url_shortener.iam.domain.exception.user;

import com.app.url_shortener.shared.exception.conflict.ConflictException;

public class UserAccountLockedException extends ConflictException {

  public UserAccountLockedException() {
    super(UserErrorCode.USER_ACCOUNT_BLOCKED);
  }
}
