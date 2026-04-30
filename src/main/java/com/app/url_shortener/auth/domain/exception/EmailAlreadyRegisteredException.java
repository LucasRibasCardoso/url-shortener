package com.app.url_shortener.auth.domain.exception;

import com.app.url_shortener.shared.exception.conflict.ConflictException;

public class EmailAlreadyRegisteredException extends ConflictException {

  public EmailAlreadyRegisteredException() {
    super(AuthErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
  }
}
