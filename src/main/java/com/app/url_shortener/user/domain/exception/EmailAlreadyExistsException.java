package com.app.url_shortener.user.domain.exception;

import com.app.url_shortener.shared.exception.ErrorCode;
import com.app.url_shortener.shared.exception.conflict.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {

  @Override
  public ErrorCode getErrorCode() {
    return super.getErrorCode();
  }

  public EmailAlreadyExistsException() {
    super(UserErrorCode.USER_EMAIL_ALREADY_EXISTS);
  }
}
