package com.app.url_shortener.iam.domain.exception;

import com.app.url_shortener.shared.exception.notfound.NotFoundException;

public class EmailVerificationTokenExpiredException extends NotFoundException {


  public EmailVerificationTokenExpiredException() {
    super(AuthErrorCode.AUTH_EMAIL_VERIFICATION_TOKEN_EXPIRED);
  }
}
