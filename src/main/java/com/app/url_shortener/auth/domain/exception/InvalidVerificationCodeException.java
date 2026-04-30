package com.app.url_shortener.auth.domain.exception;

import com.app.url_shortener.shared.exception.validation.DomainValidationException;

public class InvalidVerificationCodeException extends DomainValidationException {

  public InvalidVerificationCodeException() {
    super(AuthErrorCode.AUTH_INVALID_VERIFICATION_CODE);
  }
}
