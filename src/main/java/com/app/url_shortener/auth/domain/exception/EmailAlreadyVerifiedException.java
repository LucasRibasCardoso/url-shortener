package com.app.url_shortener.auth.domain.exception;

import com.app.url_shortener.shared.exception.conflict.ConflictException;

public class EmailAlreadyVerifiedException extends ConflictException {

  public EmailAlreadyVerifiedException() {
    super(AuthErrorCode.AUTH_EMAIL_ALREADY_VERIFIED);
  }
}
